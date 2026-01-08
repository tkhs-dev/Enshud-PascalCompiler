package enshud.s4.compiler.generator.impls.casl.assembly.instructions;

import java.util.Optional;

public class JPL extends JmpInstr {
    public JPL(String label) {
        super(label);
    }

    @Override
    public boolean isBranch() {
        return true;
    }

    @Override
    public boolean isConditionalBranch() {
        return true;
    }

    @Override
    public Optional<String> getBranchTarget() {
        return Optional.of(label);
    }

    @Override
    public boolean isTerminal() {
        return false;
    }
}
