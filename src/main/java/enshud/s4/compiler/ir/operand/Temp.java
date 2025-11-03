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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Temp temp = (Temp) obj;
        return name.equals(temp.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
