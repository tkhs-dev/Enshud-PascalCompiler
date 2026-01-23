package enshud.s4.compiler.generator.impls.cproc.tir.operand;

import enshud.s4.compiler.generator.AllocatableRegister;
import enshud.s4.compiler.generator.Register;

import java.util.Optional;

public class VirtualRegister extends TIROperand implements AllocatableRegister {
    private final String name;
    private int spillCost;
    private Register predefined;

    public VirtualRegister(String name) {
        this.name = name;
        spillCost = 1;
    }

    public String getName() {
        return name;
    }

    public void setSpillCost(int cost) {
        this.spillCost = cost;
    }

    public void setPredefined(Register reg) {
        this.predefined = reg;
    }

    @Override
    public Optional<Register> getPredefined() {
        return Optional.ofNullable(predefined);
    }

    @Override
    public double getSpillCost() {
        return spillCost;
    }

    @Override
    public boolean needsGeneralPurpose() {
        return false;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof VirtualRegister)) return false;
        VirtualRegister other = (VirtualRegister) obj;
        return this.name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
