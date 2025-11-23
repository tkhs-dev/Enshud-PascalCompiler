package enshud.s4.compiler.generator.impls.casl.tir.operand;

public abstract class Immediate<T> extends TIROperand {
    public final T value;

    public static final Immediate<Integer> ZERO = new NumericImmediate(0, NumericImmediate.Type.DECIMAL);
    public static final Immediate<Integer> ONE = new NumericImmediate(1, NumericImmediate.Type.DECIMAL);
    public static final Immediate<Integer> TRUE = ONE;
    public static final Immediate<Integer> FALSE = ZERO;

    public Immediate(T value) {
        this.value = value;
    }
}
