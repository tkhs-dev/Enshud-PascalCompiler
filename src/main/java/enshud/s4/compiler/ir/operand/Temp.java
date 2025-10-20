package enshud.s4.compiler.ir.operand;

public class Temp extends Operand {
    public final String name;

    public Temp(String name) {
        this.name = name;
    }

    public static Temp of(String name) {
        return new Temp(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
