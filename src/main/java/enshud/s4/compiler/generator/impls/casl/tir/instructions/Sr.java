package enshud.s4.compiler.generator.impls.casl.tir.instructions;

import enshud.s4.compiler.generator.impls.casl.tir.operand.TIROperand;

public class Sr extends Instr<Sr> {
    public final TIROperand left;
    public final TIROperand right;

    public Sr(TIROperand left, TIROperand right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public <T> T accept(ITIRVisitor<T> visitor) {
        return null;
    }

    @Override
    public String toString() {
        return formatInstruction(left, right);
    }
}
