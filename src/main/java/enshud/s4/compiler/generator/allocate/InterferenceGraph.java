package enshud.s4.compiler.generator.allocate;

import enshud.s4.compiler.generator.impls.casl.tir.operand.RegisterOperand;

import java.util.*;

public class InterferenceGraph {
    private final Map<RegisterOperand, Set<RegisterOperand>> adjacencyList = new HashMap<>();

    public void addNode(RegisterOperand vreg) {
        adjacencyList.putIfAbsent(vreg, new HashSet<>());
    }

    public void addEdge(RegisterOperand vreg1, RegisterOperand vreg2) {
        if (vreg1.equals(vreg2)) {
            return; // 自己ループは追加しない
        }
        addNode(vreg1);
        addNode(vreg2);
        adjacencyList.get(vreg1).add(vreg2);
        adjacencyList.get(vreg2).add(vreg1);
    }

    public Set<RegisterOperand> getNeighbors(RegisterOperand vreg) {
        return Collections.unmodifiableSet(adjacencyList.getOrDefault(vreg, Collections.emptySet()));
    }

    public Set<RegisterOperand> getNodes() {
        return Collections.unmodifiableSet(adjacencyList.keySet());
    }
}
