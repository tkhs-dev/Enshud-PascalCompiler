package enshud.s4.compiler.generator.impls.casl.tir.operand;

public abstract class Immediate<T> extends TIROperand {
    public final T value;

    public static final Immediate<Integer> ZERO = new NumericImmediate(0, NumericImmediate.Type.DECIMAL);
    public static final Immediate<Integer> ONE = new NumericImmediate(1, NumericImmediate.Type.DECIMAL);
    public static final AddressOperand TRUE = new AddressOperand("TRUE");
    public static final AddressOperand FALSE = new AddressOperand("FALSE");

    public Immediate(T value) {
        this.value = value;
    }
}
