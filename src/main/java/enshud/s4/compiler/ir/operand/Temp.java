package enshud.s4.compiler.ir.operand;

public class Temp extends Symbol{
    public Temp(String name) {
        super(name);
    }

    public static Temp of(String name){
        return new Temp(name);
    }
}
