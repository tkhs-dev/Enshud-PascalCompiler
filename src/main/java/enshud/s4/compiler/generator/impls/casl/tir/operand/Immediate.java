package enshud.s4.compiler.generator.impls.casl.tir.operand;

public abstract class Immediate<T> extends TIROperand {
    public final T value;

    public Immediate(T value) {
        this.value = value;
    }
}
