package enshud.s4.compiler.ir.operand;

public class Variable extends IROperand {
    public final String name;

    public Variable(String name) {
        this.name = name;
    }

    public static Variable of(String name) {
        return new Variable(name);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Variable variable = (Variable) obj;
        return name.equals(variable.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
