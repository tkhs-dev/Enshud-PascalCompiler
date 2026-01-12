package enshud.s4.compiler.ir.operand;

public class Temp extends IROperand {
    public final String id;

    public Temp(String id) {
        this.id = id;
    }

    public static Temp of(String id){
        return new Temp(id);
    }

    @Override
    public String toString() {
        return id;
    }
}
