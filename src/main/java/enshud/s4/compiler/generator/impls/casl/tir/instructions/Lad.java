package enshud.s4.compiler.generator.impls.casl.tir.instructions;

import enshud.s4.compiler.generator.impls.casl.tir.operand.TIROperand;

public class Lad extends TIRInstruction<Lad> {
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
}
