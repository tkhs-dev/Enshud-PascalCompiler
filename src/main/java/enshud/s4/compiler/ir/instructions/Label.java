package enshud.s4.compiler.ir.instructions;

import enshud.s4.compiler.ir.IIrVisitor;

import java.util.Optional;

public class Label extends IRInstruction {
    public final String name;

    public Label(String name) {
        this.name = name;
        this.setLabel(name);
    }

    public static Label of(String name) {
        return new Label(name);
    }

    @Override
    public <T> T accept(IIrVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean isBranch() {
        return false;
    }

    @Override
    public boolean isConditionalBranch() {
        return false;
    }

    @Override
    public Optional<String> getBranchTarget() {
        return Optional.empty();
    }

    @Override
    public boolean isTerminal() {
        return false;
    }

    @Override
    public String toString() {
        return String.format("%s:", name);
    }
}
