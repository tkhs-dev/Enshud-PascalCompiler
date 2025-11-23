package enshud.s4.compiler.generator.impls.casl.tir.instructions;

public class Jmp extends TIRInstruction<Jmp> {
    public final String addressLabel;
    public final Type type;

    public Jmp(String addressLabel, Type type) {
        this.addressLabel = addressLabel;
        this.type = type;
    }

    @Override
    public <T> T accept(ITIRVisitor<T> visitor) {
        return null;
    }

    @Override
    public String toString() {
        return formatInstruction(addressLabel, type);
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
