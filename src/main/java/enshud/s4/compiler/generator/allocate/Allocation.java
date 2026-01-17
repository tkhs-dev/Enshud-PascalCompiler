package enshud.s4.compiler.generator.allocate;

import enshud.s4.compiler.generator.AllocatableRegister;
import enshud.s4.compiler.generator.Register;

import java.util.Map;
import java.util.Set;

public class Allocation<T extends AllocatableRegister> {
    private final Map<T, Register> allocationMap;
    private final Set<T> spilled;

    public Allocation(Map<T, Register> allocationMap, Set<T> spilled) {
        this.allocationMap = allocationMap;
        this.spilled = spilled;
    }

    public Map<T, Register> getAllocationMap() {
        return allocationMap;
    }

    public Set<T> getSpilled() {
        return spilled;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Allocation:\n");
        for (Map.Entry<T, Register> entry : allocationMap.entrySet()) {
            sb.append("  ").append(entry.getKey()).append(" -> ").append(entry.getValue()).append("\n");
        }
        sb.append("Spilled Registers:\n");
        for (T reg : spilled) {
            sb.append("  ").append(reg).append("\n");
        }
        return sb.toString();
    }
}
