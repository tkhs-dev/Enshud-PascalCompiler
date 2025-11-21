package enshud.s4.compiler.generator.impls.casl.tir.operand;

public class NumericImmediate extends Immediate<Integer> {
    public final Type type;

    public NumericImmediate(Integer value, Type type) {
        super(value);
        this.type = type;
    }

    public enum Type {
        HEXA,
        DECIMAL
    }
}
