package enshud.s4.compiler.generator.impls.casl.tir.instructions;

import enshud.s4.compiler.generator.AllocatableRegister;
import enshud.s4.compiler.generator.impls.casl.tir.operand.RegisterOperand;
import enshud.s4.compiler.generator.impls.casl.tir.operand.TIROperand;

import java.util.Set;

public class Push extends Instr<Push> {
    public final TIROperand src;

    public Push(TIROperand src) {
        this.src = src;
    }

    @Override
    public <T> T accept(ITIRVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return formatInstruction(src);
    }

    @Override
    public Set<AllocatableRegister> getUse() {
        return registerSet(src);
    }

    @Override
    public Set<AllocatableRegister> getDef() {
        return Set.of();
    }
}
