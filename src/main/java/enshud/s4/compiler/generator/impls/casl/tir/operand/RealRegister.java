package enshud.s4.compiler.generator.impls.casl.tir.operand;

import enshud.s4.compiler.generator.Register;

import java.util.Optional;

public class RealRegister extends RegisterOperand {
    public final Register register;

    public RealRegister(Register register) {
        this.register = register;
    }

    public static RealRegister of(Register register) {
        return new RealRegister(register);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RealRegister that = (RealRegister) obj;
        return register.equals(that.register);
    }

    @Override
    public int hashCode() {
        return register.hashCode();
    }

    @Override
    public String toString() {
        return register.toString();
    }

    @Override
    public Optional<Register> getPredefined() {
        return Optional.of(register);
    }
}
