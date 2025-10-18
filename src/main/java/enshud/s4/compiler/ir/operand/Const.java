package enshud.s4.compiler.ir.operand;

public class Const extends Operand {
    public final int value;

    public Const(int value) {
        this.value = value;
    }
}
