package enshud.s4.compiler.generator.allocate;

import enshud.s4.compiler.generator.AllocatableRegister;
import enshud.s4.compiler.generator.InterferenceGraph;
import enshud.s4.compiler.generator.Register;

import java.util.*;

public class RegisterAllocator extends Allocator{
    private final List<Register> allocatableRegisters;

    // VirtualRegister → Register
    private final Map<AllocatableRegister, Register> allocation = new HashMap<>();

    // Spill が必要な register
    private final Set<AllocatableRegister> spilled = new HashSet<>();

    // 彩色用スタック（Simplifyフェーズ）
    private final Deque<AllocatableRegister> stack = new ArrayDeque<>();

    public RegisterAllocator(InterferenceGraph graph, TargetDescription targetDescription) {
        super(graph, targetDescription);
        this.allocatableRegisters = targetDescription.getAllocatableRegisters();
    }

    public Map<AllocatableRegister, Register> getAllocation() {
        return allocation;
    }

    public Set<AllocatableRegister> getSpilledRegisters() {
        return spilled;
    }

    //-------------------------------------------------------------
    // メイン処理
    //-------------------------------------------------------------
    public Allocation allocate() {
        simplifyPhase();
        spillPhase();
        selectPhase();
        return new Allocation(allocation, spilled);
    }

    //-------------------------------------------------------------
    // 1. Simplify (degree < K のノードを削除して stack に積む)
    //-------------------------------------------------------------
    private void simplifyPhase() {
        while (true) {
            AllocatableRegister removable = findRemovableNode();
            if (removable == null) break;

            graph.removeNode(removable);
            stack.push(removable);
        }
    }

    private AllocatableRegister findRemovableNode() {
        int K = allocatableRegisters.size();

        for (AllocatableRegister r : graph.getNodes()) {

            // 事前割当レジスタは除外（色固定なので Simplify しない）
            if (r.getPredefined().isPresent()) continue;

            if (graph.getAdjacents(r).size() < K) {
                return r;
            }
        }
        return null;
    }

    //-------------------------------------------------------------
    // 2. Spill すべきノード（degree >= K かつ簡約できなかったもの）
    //-------------------------------------------------------------
    private void spillPhase() {
        for (AllocatableRegister r : graph.getNodes()) {
            if (r.getPredefined().isPresent()) continue;
            spilled.add(r);
        }
    }

    //-------------------------------------------------------------
    // 3. Select (stack から取り出して色を割り当てる)
    //-------------------------------------------------------------
    private void selectPhase() {

        while (!stack.isEmpty()) {
            AllocatableRegister r = stack.pop();
            graph.restoreNode(r);

            // 事前割り当てレジスタは固定
            if (r.getPredefined().isPresent()) {
                allocation.put(r, r.getPredefined().get());
                continue;
            }

            Register color = chooseColor(r);

            if (color == null) {
                spilled.add(r);
            } else {
                allocation.put(r, color);
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

        // 利用可能な色から未使用のものを選択
        for (Register reg : allocatableRegisters) {
            if (!used.contains(reg)) return reg;
        }

        return null; // Spill 必要
    }
}