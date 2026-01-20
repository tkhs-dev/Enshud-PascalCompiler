package enshud.s4.compiler.generator.impls.cproc.assembly.instructions;

import enshud.s4.compiler.generator.impls.cproc.assembly.operand.LabelOperand;

import java.util.Optional;

public class SETIX extends Instr1Op<SETIX, LabelOperand> {
    public SETIX(LabelOperand operand) {
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
