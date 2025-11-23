package enshud.s4.compiler.generator.impls.casl.tir.instructions;

public class Jmp extends TIRInstruction<Jmp> {
    public final String label;
    public final Type type;

    public Jmp(String label, Type type) {
        this.label = label;
        this.type = type;
    }

    @Override
    public <T> T accept(ITIRVisitor<T> visitor) {
        return null;
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
