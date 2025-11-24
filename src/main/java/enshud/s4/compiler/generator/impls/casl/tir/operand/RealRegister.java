package enshud.s4.compiler.generator.impls.casl.tir.operand;

import enshud.s4.compiler.generator.Register;

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
    public String toString() {
        return register.toString();
    }
}
