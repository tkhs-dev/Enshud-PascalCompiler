package enshud.s4.compiler.generator.impls.casl.assembly.instructions;

import java.util.List;
import java.util.Optional;

public class DC extends AssemblyInstr<DC> {
    private final Object value;

    public DC(String value) {
        this.value = value;
    }

    public DC(int value) {
        this.value = value;
    }

    private String formatValue() {
        if (value instanceof String) {
            return String.format("'%s'", value);
        } else {
            return value.toString();
        }
    }

    @Override
    public String toString() {
        return String.format("%s %s", super.toString(), formatValue() );
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
}
