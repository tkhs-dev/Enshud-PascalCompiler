package enshud.s4.compiler.generator.allocate;

import enshud.s4.compiler.generator.*;
import enshud.s4.compiler.instruction.FunctionUnit;

import java.util.*;

public class RegisterAllocator<T extends AllocatableRegister> extends Allocator<T>{
    private final TargetDescription description;

    // 干渉グラフ
    protected final InterferenceGraph<T> graph;

    // コピーされたレジスタの集合と対応する実レジスタ
    protected final Map<Set<T>, Register> copiedRegisterSets = new HashMap<>();

    // VirtualRegister → Register
    private final Map<T, Register> allocation = new HashMap<>();

    // Spill が必要な register
    private final Set<T> spilled = new HashSet<>();

    // 彩色用スタック（Simplifyフェーズ）
    private final Deque<T> stack = new ArrayDeque<>();

    public <I extends AnalysableInstruction<T>> RegisterAllocator(FunctionUnit<I> function, TargetDescription targetDescription) {
        super(function, targetDescription);
        this.description = targetDescription;
        var liveness = new LivenessAnalyzer<T, I>(function).analyze();
        this.graph = InterferenceGraph.build(function, liveness, targetDescription);
    }

    public Map<T, Register> getAllocation() {
        return allocation;
    }

    public Set<T> getSpilled() {
        return spilled;
    }

    //-------------------------------------------------------------
    // メイン処理
    //-------------------------------------------------------------
    public Allocation<T> allocate() {
        collectCopiedRegisters();
        System.out.println("Copied register sets: " + copiedRegisterSets.keySet());
        buildStack();
        selectPhase();
        // リロード退避用のスピルを求める
        return new Allocation<>(allocation, spilled);
    }

    private void collectCopiedRegisters() {
        for (var instr : function.getInstructions()) {
            var copied = ((AnalysableInstruction<T>)instr).getCopiedRegisters();
            // 共通部分がある場合はマージする
            boolean merged = false;
            for (Set<T> set : copiedRegisterSets.keySet()) {
                Set<T> intersection = new HashSet<>(set);
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

    private T findRemovableNode() {
        int K = description.getAllocatableRegisters().size();

        for (var r : graph.getNodes()) {
            // 事前割当レジスタかつ専用レジスタの場合除外
            if (r.getPredefined().isPresent()){
                var preReg = r.getPredefined().get();
                if(target.getReservedRegisters().contains(preReg)) {
                    return r;
                }
            }
        }

        for (T r : graph.getNodes()) {
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
        var removeList = new ArrayList<T>();
        // 汎用レジスタが必要な場合は先に処理
        for (var r : stack){
            if (!r.needsGeneralPurpose() || r.getPredefined().isPresent()){
                continue;
            }
            removeList.add(r);
            Register color = chooseColor(r, true);
            if (color == null) {
                spilled.add(r);
            } else {
                System.out.println("Pre-assigning general-purpose needed register color: " + color + " to " + r);
                allocateRealRegister(r, color);
            }
        }
        for (var r : removeList){
            stack.remove(r);
        }
        removeList.clear();

        // コピーが存在する場合は先に処理
        for (var r : stack){
            if (r.getPredefined().isPresent()){
                continue;
            }
            var regSets = copiedRegisterSets.keySet();
            for (var regSet : regSets) {
                if (regSet.contains(r)) {
                    Register assignedColor = chooseColor(r, false);
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
            T r = stack.pop();

            // 事前割り当てレジスタは固定
            if (r.getPredefined().isPresent()) {
                allocateRealRegister(r, r.getPredefined().get());
                continue;
            }

            Register color = chooseColor(r, false);

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
    private Register chooseColor(T r, boolean needsGeneralPurpose) {
        Set<T> adjs = graph.getAdjacents(r);

        Map<Register, T> used = new HashMap<>();

        // 隣接ノードが使用している色を除外
        for (T neigh : adjs) {

            // 事前割り当て色
            neigh.getPredefined().ifPresent(x -> used.put(x, neigh));

            // 既に割り当て済み
            Register c = allocation.get(neigh);
            if (c != null) used.put(c, neigh);
        }

        // コピーでかつ色が決まっている場合は優先して使用
        for (var entry : copiedRegisterSets.entrySet()) {
            Set<T> regSet = entry.getKey();
            Register assignedColor = entry.getValue();

            if (assignedColor != null && regSet.contains(r)) {
                System.out.println("Trying to assign copied register set color: " + assignedColor + " to " + r + " from " + regSet);
                // もしregSetに含まれない隣接ノードがあれば使用不可
                if(used.containsKey(assignedColor) && !regSet.contains(used.get(assignedColor))) {
                    System.out.println("  But color " + assignedColor + " is already used by " + used.get(assignedColor));
                    continue;
                }
                return assignedColor;
            }
        }

        List<Register> candidateRegisters;
        if (needsGeneralPurpose) {
            candidateRegisters = description.getScratchCandidates();
        } else {
            candidateRegisters = description.getAllocatableRegisters();
        }

        // 利用可能な色から未使用のものを選択
        for (Register reg : candidateRegisters) {
            if (!used.containsKey(reg)) return reg;
        }

        return null; // Spill 必要
    }

    private void buildStack() {
        while (!graph.getNodes().isEmpty()) {

            T removable = findRemovableNode();

            if (removable != null) {
                graph.removeNode(removable);
                stack.push(removable);
            } else {
                // Simplify 不可 → spill 候補を 1 つ選ぶ
                T victim = chooseSpillCandidate();
                graph.removeNode(victim);
                spilled.add(victim);
            }
        }
    }

    private T chooseSpillCandidate() {
        var reg = graph.getNodes().stream().min(Comparator.comparingDouble(T::getSpillCost));
        if (reg.isPresent()) {
            return reg.get();
        }
        throw new IllegalStateException("No spill candidate");
    }

    private void allocateRealRegister(T r, Register realReg) {
        allocation.put(r, realReg);

        // コピーされたレジスタ集合に割り当てを記録
        for (var entry : copiedRegisterSets.entrySet()) {
            Set<T> regSet = entry.getKey();
            if (regSet.contains(r)) {
                copiedRegisterSets.put(regSet, realReg);
                break;
            }
        }
    }
}