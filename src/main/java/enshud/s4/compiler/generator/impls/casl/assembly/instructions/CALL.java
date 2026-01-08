package enshud.s4.compiler.generator.impls.casl.assembly.instructions;

import java.util.Optional;

public class CALL extends JmpInstr{
    public CALL(String operand) {
        super(operand);
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
