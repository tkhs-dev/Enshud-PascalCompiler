package enshud.s4.compiler.generator.impls.casl.tir.instructions;

import enshud.s4.compiler.generator.impls.casl.tir.operand.TIROperand;

public class And extends TIRInstruction {
    public final TIROperand left;
    public final TIROperand right;

    public And(TIROperand left, TIROperand right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public <T> T accept(ITIRVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
