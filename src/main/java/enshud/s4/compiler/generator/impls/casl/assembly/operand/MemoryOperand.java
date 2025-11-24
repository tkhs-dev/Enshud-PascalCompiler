package enshud.s4.compiler.generator.impls.casl.assembly.operand;

import enshud.s4.compiler.generator.impls.casl.Registers;

import java.util.Optional;

public class MemoryOperand implements DataOperand {
    private final String address;
    private final Optional<Registers> offsetRegister;

    public MemoryOperand(String address, Optional<Registers> offsetRegister) {
        this.address = address;
        this.offsetRegister = offsetRegister;
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
