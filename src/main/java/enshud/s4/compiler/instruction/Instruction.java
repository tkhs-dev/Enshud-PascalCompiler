package enshud.s4.compiler.instruction;

import java.util.Optional;

public abstract class Instruction {
    protected String label = null;

    public void setLabel(String label) {
        this.label = label;
    }

    public Optional<String> getLabel() {
        return Optional.ofNullable(label);
    }

    public void clearLabel() {
        this.label = null;
    }
}
