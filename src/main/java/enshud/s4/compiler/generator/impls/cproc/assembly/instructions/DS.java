package enshud.s4.compiler.generator.impls.cproc.assembly.instructions;

import java.util.Optional;

public class DS extends AssemblyInstr<DS> {
    private final int size;

    public DS(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
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
        return String.format("%s %d", super.toString(), size);
    }
}
