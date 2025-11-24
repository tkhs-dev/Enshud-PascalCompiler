package enshud.s4.compiler.generator.impls.casl.tir.instructions;

import enshud.s4.compiler.generator.impls.casl.tir.operand.TIROperand;

public class Ld extends Instr<Ld> {
    public final TIROperand dest;
    public final TIROperand src;

    public Ld(TIROperand dest, TIROperand src) {
        this.dest = dest;
        this.src = src;
    }

    @Override
    public <T> T accept(ITIRVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return formatInstruction(dest, src);
    }
}
