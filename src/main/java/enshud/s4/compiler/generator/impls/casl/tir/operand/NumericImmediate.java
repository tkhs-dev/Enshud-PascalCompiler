package enshud.s4.compiler.generator.impls.casl.tir.operand;

public class NumericImmediate extends Immediate<Integer> {
    public final Type type;

    public NumericImmediate(Integer value, Type type) {
        super(value);
        this.type = type;
    }

    @Override
    public String toString() {
        switch (type) {
            case HEXA:
                return String.format("0x%X", value);
            case DECIMAL:
            default:
                return String.format("%d", value);
        }
    }

    public enum Type {
        HEXA,
        DECIMAL
    }
}
