package enshud.s4.compiler.generator.impls.casl.tir.operand;

import enshud.s4.compiler.generator.Register;

public class RealRegister extends TIROperand {
    public final Register register;

    public RealRegister(Register register) {
        this.register = register;
    }

    public static RealRegister of(Register register) {
        return new RealRegister(register);
    }

    @Override
    public String toString() {
        return register.toString();
    }
}
