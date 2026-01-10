package enshud.s4.compiler.generator.allocate;

import enshud.s4.compiler.generator.*;
import enshud.s4.compiler.instruction.FunctionUnit;
import enshud.s4.compiler.instruction.Instruction;

import java.util.*;

public class RegisterAllocator extends Allocator{
    private final List<Register> allocatableRegisters;

    // 干渉グラフ
    protected final InterferenceGraph graph;

    // コピーされたレジスタの集合と対応する実レジスタ
    protected final Map<Set<AllocatableRegister>, Register> copiedRegisterSets = new HashMap<>();

    // VirtualRegister → Register
    private final Map<AllocatableRegister, Register> allocation = new HashMap<>();

    // Spill が必要な register
    private final Set<AllocatableRegister> spilled = new HashSet<>();

    // 彩色用スタック（Simplifyフェーズ）
    private final Deque<AllocatableRegister> stack = new ArrayDeque<>();

    public RegisterAllocator(FunctionUnit<? extends AnalysableInstruction> function, TargetDescription targetDescription) {
        super(function, targetDescription);
        this.allocatableRegisters = targetDescription.getAllocatableRegisters();
        var liveness = new LivenessAnalyzer<>(function).analyze();
        this.graph = InterferenceGraph.build(function, liveness, targetDescription);
    }

    public Map<AllocatableRegister, Register> getAllocation() {
        return allocation;
    }

    public Set<AllocatableRegister> getSpilled() {
        return spilled;
    }

    //-------------------------------------------------------------
    // メイン処理
    //-------------------------------------------------------------
    public Allocation allocate() {
        collectCopiedRegisters();
        System.out.println("Copied register sets: " + copiedRegisterSets.keySet());
        buildStack();
        selectPhase();
        // リロード退避用のスピルを求める
        return new Allocation(allocation, spilled);
    }

    private void collectCopiedRegisters() {
        for (var instr : function.getInstructions()) {
            var copied = ((AnalysableInstruction)instr).getCopiedRegisters();
            // 共通部分がある場合はマージする
            boolean merged = false;
            for (Set<AllocatableRegister> set : copiedRegisterSets.keySet()) {
                Set<AllocatableRegister> intersection = new HashSet<>(set);
                intersection.retainAll(copied);
                if (!intersection.isEmpty()) {
                    copied.addAll(set);
                    merged = true;
                    break;
                }
            }
            // どのセットとも共通部分がなければ新規追加
            if (!merged) {
                copiedRegisterSets.put(copied, null);
            }
        }
    }

    private AllocatableRegister findRemovableNode() {
        int K = allocatableRegisters.size();

        for (AllocatableRegister r : graph.getNodes()) {
            // 事前割当レジスタかつ専用レジスタの場合除外
            if (r.getPredefined().isPresent()){
                var preReg = r.getPredefined().get();
                if(target.getReservedRegisters().contains(preReg)) {
                    return r;
                }
            }
        }

        for (AllocatableRegister r : graph.getNodes()) {
            if (graph.getAdjacents(r).size() < K) {
                return r;
            }
        }
        return null;
    }

    //-------------------------------------------------------------
    // 3. Select (stack から取り出して色を割り当てる)
    //-------------------------------------------------------------
    private void selectPhase() {
        graph.restoreAll();
        // コピーが存在する場合は先に処理
        var removeList = new ArrayList<AllocatableRegister>();
        for (var r : stack){
            if (r.getPredefined().isPresent()){
                continue;
            }
            var regSets = copiedRegisterSets.keySet();
            for (var regSet : regSets) {
                if (regSet.contains(r)) {
                    Register assignedColor = chooseColor(r);
                    System.out.println("Pre-assigning copied register set color: " + assignedColor + " to " + r);
                    allocateRealRegister(r, assignedColor);
                    removeList.add(r);
                    break;
                }
            }
        }
        for (var r : removeList){
            stack.remove(r);
        }
        while (!stack.isEmpty()) {
            AllocatableRegister r = stack.pop();

            // 事前割り当てレジスタは固定
            if (r.getPredefined().isPresent()) {
                allocateRealRegister(r, r.getPredefined().get());
                continue;
            }

            Register color = chooseColor(r);

            if (color == null) {
                spilled.add(r);
            } else {
                allocateRealRegister(r, color);
            }
        }
    }

    //-------------------------------------------------------------
    // 利用可能な実レジスタを選択
    //-------------------------------------------------------------
    private Register chooseColor(AllocatableRegister r) {
        Set<AllocatableRegister> adjs = graph.getAdjacents(r);

        Set<Register> used = new HashSet<>();

        // 隣接ノードが使用している色を除外
        for (AllocatableRegister neigh : adjs) {

            // 事前割り当て色
            neigh.getPredefined().ifPresent(used::add);

            // 既に割り当て済み
            Register c = allocation.get(neigh);
            if (c != null) used.add(c);
        }

        // コピーでかつ色が決まっている場合は優先して使用
        for (var entry : copiedRegisterSets.entrySet()) {
            Set<AllocatableRegister> regSet = entry.getKey();
            Register assignedColor = entry.getValue();

            if (assignedColor != null && regSet.contains(r)) {
                System.out.println("Trying to assign copied register set color: " + assignedColor + " to " + r);
                // 隣接ノードが同じ色を使っていなければその色を使う
                if (!used.contains(assignedColor)) {
                    return assignedColor;
                }
                System.out.println("  but color is used by adjacent, skipping.");
            }
        }

        // 利用可能な色から未使用のものを選択
        for (Register reg : allocatableRegisters) {
            if (!used.contains(reg)) return reg;
        }

        return null; // Spill 必要
    }

    private void buildStack() {
        while (!graph.getNodes().isEmpty()) {

            AllocatableRegister removable = findRemovableNode();

            if (removable != null) {
                graph.removeNode(removable);
                stack.push(removable);
            } else {
                // Simplify 不可 → spill 候補を 1 つ選ぶ
                AllocatableRegister victim = chooseSpillCandidate();
                graph.removeNode(victim);
                spilled.add(victim);
            }
        }
    }

    private AllocatableRegister chooseSpillCandidate() {
        var reg = graph.getNodes().stream().min(Comparator.comparingDouble(AllocatableRegister::getSpillCost));
        if (reg.isPresent()) {
            return reg.get();
        }
        throw new IllegalStateException("No spill candidate");
    }

    private void allocateRealRegister(AllocatableRegister r, Register realReg) {
        allocation.put(r, realReg);

        // コピーされたレジスタ集合に割り当てを記録
        for (var entry : copiedRegisterSets.entrySet()) {
            Set<AllocatableRegister> regSet = entry.getKey();
            if (regSet.contains(r)) {
                copiedRegisterSets.put(regSet, realReg);
                break;
            }
        }
    }
}