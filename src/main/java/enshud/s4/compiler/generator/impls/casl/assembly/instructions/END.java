package enshud.s4.compiler.generator.impls.casl.assembly.instructions;

import java.util.Optional;

public class END extends AssemblyInstr<END> {
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
        return true;
    }
}
