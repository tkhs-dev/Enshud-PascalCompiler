package enshud.s4.compiler.generator.impls.casl.tir.instructions;

import enshud.s4.compiler.generator.impls.casl.tir.operand.TIROperand;

public class St extends Instr<St> {
    public final TIROperand src;
    public final TIROperand dest;

    public St(TIROperand src, TIROperand dest) {
        this.src = src;
        this.dest = dest;
    }

    @Override
    public <T> T accept(ITIRVisitor<T> visitor) {
        return null;
    }

    @Override
    public String toString() {
        return formatInstruction(src, dest);
    }
}
