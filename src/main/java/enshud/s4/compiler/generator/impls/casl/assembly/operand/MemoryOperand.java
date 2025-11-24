package enshud.s4.compiler.generator.impls.casl.assembly.operand;

import enshud.s4.compiler.generator.Register;

import java.util.Optional;

public class MemoryOperand implements DataOperand {
    private final String address;
    private final Optional<Register> offsetRegister;

    public MemoryOperand(String address, Register offsetRegister) {
        this.address = address;
        this.offsetRegister = Optional.ofNullable(offsetRegister);
    }

    public MemoryOperand(String address) {
        this.address = address;
        this.offsetRegister = Optional.empty();
    }

    @Override
    public String toString() {
        if (offsetRegister.isPresent()) {
            return String.format("%s, %s", address, offsetRegister.get());
        } else {
            return String.format("%s", address);
        }
    }
}
