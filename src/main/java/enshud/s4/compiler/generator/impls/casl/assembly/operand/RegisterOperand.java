package enshud.s4.compiler.generator.impls.casl.assembly.operand;

import enshud.s4.compiler.generator.impls.casl.Registers;

public class RegisterOperand implements DataOperand {
    public final Registers register;

    public RegisterOperand(Registers register) {
        this.register = register;
    }

    @Override
    public String toString() {
        return register.toString();
    }
}
