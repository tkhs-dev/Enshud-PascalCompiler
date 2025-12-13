package enshud.s4.compiler.generator.impls.casl.tir.instructions;

import enshud.s4.compiler.generator.AllocatableRegister;
import enshud.s4.compiler.generator.impls.casl.tir.operand.TIROperand;

import java.util.Set;

public class Lad extends TIrInstr<Lad> {
    public final TIROperand dest;
    public final TIROperand value;

    public Lad(TIROperand dest, TIROperand value) {
        this.dest = dest;
        this.value = value;
    }

    @Override
    public <T> T accept(ITIRVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return formatInstruction(dest, value);
    }

    @Override
    public Set<AllocatableRegister> getUse() {
        return registerSet(value);
    }

    @Override
    public Set<AllocatableRegister> getDef() {
        return registerSet(dest);
    }
}
