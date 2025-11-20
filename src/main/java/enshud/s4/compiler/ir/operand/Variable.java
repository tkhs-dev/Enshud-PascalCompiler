package enshud.s4.compiler.ir.operand;

public class Variable extends Operand {
    public final String name;

    public Variable(String name) {
        this.name = name;
    }

    public static Variable of(String name) {
        return new Variable(name);
    }
}
