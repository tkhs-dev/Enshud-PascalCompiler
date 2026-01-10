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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RegisterOperand that = (RegisterOperand) obj;
        return register == that.register;
    }
}
