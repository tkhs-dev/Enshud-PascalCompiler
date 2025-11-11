package enshud.s4.compiler.ir.operand;

public class Variable extends Symbol {
    public Variable(String name) {
        super(name);
    }

    public static Variable of(String name) {
        return new Variable(name);
    }
}
