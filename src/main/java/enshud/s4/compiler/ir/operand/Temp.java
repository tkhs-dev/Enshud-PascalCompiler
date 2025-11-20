package enshud.s4.compiler.ir.operand;

public class Temp extends Operand {
    public final String id;

    public Temp(String id) {
        this.id = id;
    }

    public static Temp of(String id){
        return new Temp(id);
    }
}
