package enshud.s4.compiler.generator.impls.casl.tir.instructions;

import enshud.s4.compiler.generator.impls.casl.tir.operand.RegisterOperand;
import enshud.s4.compiler.generator.impls.casl.tir.operand.TIROperand;

import java.util.Set;

public class Pop extends Instr<Pop> {
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
    public Set<RegisterOperand> getUse() {
        return registerSet();
    }

    @Override
    public Set<RegisterOperand> getDef() {
        return registerSet(target);
    }
}
