package enshud.s4.compiler.generator.impls.casl.tir.operand;

import enshud.s4.compiler.generator.AllocatableRegister;
import enshud.s4.compiler.generator.Register;
import enshud.util.SerialManager;

import java.util.Optional;

public class VirtualRegister extends TIROperand implements AllocatableRegister {
    public String id;
    public double spillCost;
    private Register predefined;

    public VirtualRegister(String id, double spillCost) {
        this.id = id;
        this.spillCost = spillCost;
        predefined = null;
    }

    public VirtualRegister(String id) {
        this(id, 1.0);
    }

    private static final SerialManager tmpCounter = new SerialManager("");
    public static VirtualRegister tmpWithPredefined(Register predefined) {
        return new VirtualRegister(tmpCounter.New()).withPredefined(predefined);
    }

    public static VirtualRegister withPredefined(String id, Register predefined) {
        return new VirtualRegister(id).withPredefined(predefined);
    }

    public VirtualRegister withPredefined(Register predefined) {
        this.predefined = predefined;
        return this;
    }

    public VirtualRegister withSpillCost(double spillCost) {
        this.spillCost = spillCost;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        VirtualRegister that = (VirtualRegister) obj;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public Optional<Register> getPredefined() {
        return Optional.ofNullable(predefined);
    }

    @Override
    public double getSpillCost() {
        return spillCost;
    }
}
