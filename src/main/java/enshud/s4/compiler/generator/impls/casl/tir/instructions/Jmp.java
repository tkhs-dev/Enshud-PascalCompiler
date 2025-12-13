package enshud.s4.compiler.generator.impls.casl.tir.instructions;

import enshud.s4.compiler.generator.AllocatableRegister;

import java.util.Set;

public class Jmp extends TIrInstr<Jmp> {
    public final String addressLabel;
    public final Type type;

    public Jmp(String addressLabel, Type type) {
        this.addressLabel = addressLabel;
        this.type = type;
    }

    @Override
    public <T> T accept(ITIRVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return formatInstruction(addressLabel, type);
    }

    @Override
    public Set<AllocatableRegister> getUse() {
        return Set.of();
    }

    @Override
    public Set<AllocatableRegister> getDef() {
        return Set.of();
    }


    public enum Type {
        PLUS,
        MINUS,
        ZERO,
        NONZERO,
        OVERFLOW,
        UNCONDITIONAL
    }
}
