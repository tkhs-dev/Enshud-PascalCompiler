package enshud.s4.compiler.generator.allocate;

import enshud.s4.compiler.generator.AllocatableRegister;
import enshud.s4.compiler.generator.Register;

import java.util.Map;
import java.util.Set;

public class Allocation {
    private final Map<AllocatableRegister, Register> allocationMap;
    private final Map<AllocatableRegister, Integer> spilledMap;

    public Allocation(Map<AllocatableRegister, Register> allocationMap, Map<AllocatableRegister, Integer> spilledMap) {
        this.allocationMap = allocationMap;
        this.spilledMap = spilledMap;
    }

    public Map<AllocatableRegister, Register> getAllocationMap() {
        return allocationMap;
    }

    public Map<AllocatableRegister, Integer> getSpilledMap() {
        return spilledMap;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Allocation:\n");
        for (Map.Entry<AllocatableRegister, Register> entry : allocationMap.entrySet()) {
            sb.append("  ").append(entry.getKey()).append(" -> ").append(entry.getValue()).append("\n");
        }
        sb.append("Spilled Registers:\n");
        for (Map.Entry<AllocatableRegister, Integer> entry : spilledMap.entrySet()) {
            sb.append("  ").append(entry.getKey()).append(" -> stack offset ").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }
}
