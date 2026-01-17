package enshud.s4.compiler.generator.impls.casl.tir.operand;

import java.util.Optional;

public abstract class AddressBasedOperand extends DataOperand{
    protected VirtualRegister offsetRegister;

    public AddressBasedOperand() {
        offsetRegister = null;
    }

    public Optional<VirtualRegister> getOffset() {
        return Optional.ofNullable(offsetRegister);
    }

    public void setOffset(VirtualRegister offsetRegister) {
        this.offsetRegister = offsetRegister;
    }
}
