package enshud.s4.compiler.generator.impls.casl.assembly.operand;

import enshud.s4.compiler.generator.Register;

public class RegisterOperand implements DataOperand {
    public final Register register;

    public RegisterOperand(Register register) {
        this.register = register;
    }

    @Override
    public String toString() {
        return register.toString();
    }
}
