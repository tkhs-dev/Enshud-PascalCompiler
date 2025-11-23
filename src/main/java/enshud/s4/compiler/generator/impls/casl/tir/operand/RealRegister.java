package enshud.s4.compiler.generator.impls.casl.tir.operand;

import enshud.s4.compiler.generator.impls.casl.Registers;

public class RealRegister extends TIROperand {
    public final Registers register;

    public RealRegister(Registers register) {
        this.register = register;
    }

    public static RealRegister of(Registers register) {
        return new RealRegister(register);
    }
}
