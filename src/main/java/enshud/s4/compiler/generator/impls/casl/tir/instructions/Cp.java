package enshud.s4.compiler.generator.impls.casl.tir.instructions;

import enshud.s4.compiler.generator.AllocatableRegister;
import enshud.s4.compiler.generator.impls.casl.tir.operand.TIROperand;

import java.util.Set;

public class Cp extends TIrInstr<Cp> {
    public final TIROperand left;
    public final TIROperand right;

    public Cp(TIROperand left, TIROperand right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public <T> T accept(ITIRVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return formatInstruction(left, right);
    }

    @Override
    public Set<AllocatableRegister> getUse() {
        return registerSet(left, right);
    }

    @Override
    public Set<AllocatableRegister> getDef() {
        return Set.of();
    }
}
