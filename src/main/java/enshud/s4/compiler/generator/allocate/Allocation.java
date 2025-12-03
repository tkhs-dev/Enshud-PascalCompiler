package enshud.s4.compiler.generator.allocate;

import enshud.s4.compiler.generator.AllocatableRegister;
import enshud.s4.compiler.generator.Register;

import java.util.Map;
import java.util.Set;

public class Allocation {
    private final Map<AllocatableRegister, Register> allocationMap;
    private final Set<AllocatableRegister> spilled;

    public Allocation(Map<AllocatableRegister, Register> allocationMap, Set<AllocatableRegister> spilled) {
        this.allocationMap = allocationMap;
        this.spilled = spilled;
    }

    public Map<AllocatableRegister, Register> getAllocationMap() {
        return allocationMap;
    }

    public Set<AllocatableRegister> getSpilled() {
        return spilled;
    }
}
