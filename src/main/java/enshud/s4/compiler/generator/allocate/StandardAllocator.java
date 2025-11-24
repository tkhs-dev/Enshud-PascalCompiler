package enshud.s4.compiler.generator.allocate;

import enshud.s4.compiler.generator.Register;
import enshud.s4.compiler.generator.impls.casl.tir.operand.RealRegister;
import enshud.s4.compiler.generator.impls.casl.tir.operand.RegisterOperand;
import enshud.s4.compiler.generator.impls.casl.tir.operand.VirtualRegister;

import java.util.*;
import java.util.stream.Collectors;

public class StandardAllocator extends Allocator{
    private final List<Register> allocatable; // order matters for deterministic behavior

    public StandardAllocator(TargetDescription targetDescription) {
        super(targetDescription);
        this.allocatable = targetDescription.getAllocatableRegisters();
    }

    @Override
    public Allocation allocate(InterferenceGraph graph) {
        int colorCount = targetDescription.getAllocatableRegisters().size();

        Map<RegisterOperand, Register> assignment = new HashMap<>();
        Set<RegisterOperand> spilled = new HashSet<>();

        // 事前設定済み
        for (var r : graph.getNodes()) {
            if (r instanceof RealRegister real) {
                var reg = real.register;
                if(!targetDescription.getAllRegisters().contains(reg)) {
                    throw new IllegalArgumentException("Register " + reg + " is not a register");
                }
                assignment.put(r, reg);
            }
        }

        Map<RegisterOperand, Set<RegisterOperand>> adj = new HashMap<>();
        for (var r : graph.getNodes()) {
            adj.put(r, new HashSet<>(graph.getNeighbors(r)));
        }

        var precolored = graph.getNodes().stream()
                .filter(r -> r instanceof RealRegister)
                .collect(Collectors.toSet());

        Deque<RegisterOperand> stack = new ArrayDeque<>();
        Set<RegisterOperand> removed = new HashSet<>(precolored);

        var workNodes = graph.getNodes().stream()
                .filter(r -> r instanceof VirtualRegister)
                .collect(Collectors.toSet());

        Map<RegisterOperand, Integer> degrees = new HashMap<>();
        for (var r : workNodes) {
            degrees.put(r, adj.getOrDefault(r, Collections.emptySet()).stream()
                    .filter(workNodes::contains)
                    .collect(Collectors.toSet()).size());
        }

        while (!workNodes.isEmpty()) {
            Optional<RegisterOperand> low = workNodes.stream()
                    .filter(r -> degrees.getOrDefault(r, 0) < colorCount)
                    .findFirst();

            if (low.isPresent()) {
                var r = low.get();
                stack.push(r);
                workNodes.remove(r);

                for (var neighbor : adj.getOrDefault(r, Collections.emptySet())) {
                    if (workNodes.contains(neighbor)) {
                        degrees.put(neighbor, degrees.get(neighbor) - 1);
                    }
                }
            } else {
                // スピル
                var s = pickSpillCandidate(workNodes, degrees);
                spilled.add(s);
                stack.push(s);
                workNodes.remove(s);
                for (var neighbor : adj.getOrDefault(s, Collections.emptySet())) {
                    if (workNodes.contains(neighbor)) {
                        degrees.put(neighbor, degrees.get(neighbor) - 1);
                    }
                }
            }
        }

        while (!stack.isEmpty()) {
            var v = (VirtualRegister) stack.pop();

            if (assignment.containsKey(v)) {
                var fixed = assignment.get(v);
                if (v.needsAddressable && !targetDescription.getScratchCandidates().contains(fixed)) {
                    spilled.add(v);
                    assignment.remove(v);
                }
            }

            Set<Register> used = new HashSet<>();
            for (var neighbor : adj.getOrDefault(v, Collections.emptySet())) {
                var r = assignment.get(neighbor);
                if (r != null) used.add(r);
            }

            var candidates = allocatable.stream()
                    .filter(r -> !used.contains(r))
                    .filter(r -> !v.needsAddressable || targetDescription.getScratchCandidates().contains(r))
                    .toList();

            if (!candidates.isEmpty()) {
                var pick = candidates.getFirst();
                assignment.put(v, pick);
                spilled.remove(v);
            } else {
                spilled.add(v);
            }
        }

        // Cast RegisterOperand to VirtualRegister in the assignment map
        Map<VirtualRegister, Register> finalAssignment = new HashMap<>();
        for (var entry : assignment.entrySet()) {
            if (entry.getKey() instanceof VirtualRegister vr) {
                finalAssignment.put(vr, entry.getValue());
            }
        }

        var finalSpilled = spilled.stream()
                .filter(r -> r instanceof VirtualRegister)
                .map(r -> (VirtualRegister) r)
                .collect(Collectors.toSet());

        return new Allocation(Collections.unmodifiableMap(finalAssignment), finalSpilled);
    }

    private RegisterOperand pickSpillCandidate(Set<RegisterOperand> workNodes, Map<RegisterOperand, Integer> degree){
        return workNodes.stream()
                .min(Comparator.<RegisterOperand>comparingDouble(r ->{
                    double deg = degree.getOrDefault(r, 0);
                    double weight = Math.max(((VirtualRegister)r).spillCost, 1.0);
                    return deg / weight;
                }).reversed())
                .orElseGet(() -> workNodes.iterator().next());
    }
}
