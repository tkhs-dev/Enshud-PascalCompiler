package enshud.s4.compiler.ir.operand;

public class Const extends Operand {
    public final Object value;

    public static Const ZERO = new Const(0);
    public static Const ONE = new Const(1);
    public static Const TRUE = new Const(true);
    public static Const FALSE = new Const(false);
    public static Const MASK_16 = new Const(0xFFFF);

    public Const(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        if (value instanceof Boolean) {
            return (Boolean) value ? "true" : "false";
        }
        return value.toString();
    }
}
