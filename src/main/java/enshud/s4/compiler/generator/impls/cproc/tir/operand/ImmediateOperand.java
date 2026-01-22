package enshud.s4.compiler.generator.impls.cproc.tir.operand;

public class ImmediateOperand extends TIROperand {
    private final int value;

    public ImmediateOperand(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("=%d", value);
    }
}
