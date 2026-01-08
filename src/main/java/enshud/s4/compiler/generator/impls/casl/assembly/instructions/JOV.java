package enshud.s4.compiler.generator.impls.casl.assembly.instructions;

import java.util.Optional;

public class JOV extends JmpInstr{
    public JOV(String operand) {
        super(operand);
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
        return Optional.of(operand);
    }

    @Override
    public boolean isTerminal() {
        return false;
    }
}
