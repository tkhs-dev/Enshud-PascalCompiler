package enshud.s4.compiler.generator.impls.casl.tir.operand;

import java.util.Optional;

public class AddressOperand extends AddressBasedOperand {
    public final String base;
    private VirtualRegister offsetRegister;

    private AddressOperand(String base, VirtualRegister offset) {
        this.base = base;
        this.setOffset(offset);
    }

    public AddressOperand(String base) {
        this(base, null);
    }

    public AddressOperand withOffset(VirtualRegister offset) {
        this.setOffset(offset);
        return this;
    }

    @Override
    public Optional<VirtualRegister> getOffset() {
        return Optional.ofNullable(offsetRegister);
    }

    @Override
    public void setOffset(VirtualRegister offsetRegister) {
        this.offsetRegister = offsetRegister;
    }

    @Override
    public String toString() {
        return offsetRegister == null ? String.format("[%s]", base) : String.format("[%s + %s]", base, offsetRegister);
    }
}
