package enshud.s4.compiler.generator.impls.casl.tir.instructions;

import enshud.s4.compiler.generator.impls.casl.tir.operand.RegisterOperand;

import java.util.Set;

public class Ret extends Instr<Ret> {
    @Override
    public <T> T accept(ITIRVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Set<RegisterOperand> getUse() {
        return Set.of();
    }

    @Override
    public Set<RegisterOperand> getDef() {
        return Set.of();
    }
}
