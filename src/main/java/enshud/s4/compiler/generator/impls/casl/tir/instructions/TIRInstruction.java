package enshud.s4.compiler.generator.impls.casl.tir.instructions;

import java.util.Optional;

public abstract class TIRInstruction<T extends TIRInstruction<T>> {
    private String label = null;

    public abstract <R> R accept(ITIRVisitor<R> visitor);

    public Optional<String> getLabel() {
        return Optional.ofNullable(label);
    }

    public T setLabel(String label) {
        this.label = label;
        return (T) this;
    }
}
