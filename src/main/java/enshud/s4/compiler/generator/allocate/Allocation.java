package enshud.s4.compiler.generator.allocate;

import enshud.s4.compiler.generator.Register;
import enshud.s4.compiler.generator.impls.casl.tir.operand.VirtualRegister;

import java.util.Map;
import java.util.Set;

public class Allocation {
    private final Map<VirtualRegister, Register> allocationMap;
    private final Set<VirtualRegister> spilled;

    public Allocation(Map<VirtualRegister, Register> allocationMap, Set<VirtualRegister> spilled) {
        this.allocationMap = allocationMap;
        this.spilled = spilled;
    }

    public Map<VirtualRegister, Register> getAllocationMap() {
        return allocationMap;
    }

    public Set<VirtualRegister> getSpilled() {
        return spilled;
    }
}
