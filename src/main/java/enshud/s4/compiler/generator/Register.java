package enshud.s4.compiler.generator;

public class Register {
    public final String name;

    public Register(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Register register = (Register) obj;
        return name.equals(register.name);
    }

    @Override
    public String toString() {
        return name;
    }
}
