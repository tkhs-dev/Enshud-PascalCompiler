package enshud.s4.compiler.generator.impls.casl.tir.instructions;

import enshud.s4.compiler.generator.AllocatableRegister;
import enshud.s4.compiler.generator.impls.casl.tir.operand.TIROperand;

import java.util.Set;

public class Pop extends TIrInstr<Pop> {
    public final TIROperand target;

    public Pop(TIROperand target) {
        this.target = target;
    }

    @Override
    public <T> T accept(ITIRVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return formatInstruction(target);
    }

    @Override
    public Set<AllocatableRegister> getUse() {
        return registerSet();
    }

    @Override
    public Set<AllocatableRegister> getDef() {
        return registerSet(target);
    }
}
