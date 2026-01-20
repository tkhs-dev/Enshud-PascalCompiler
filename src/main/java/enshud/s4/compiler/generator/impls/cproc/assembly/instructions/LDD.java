package enshud.s4.compiler.generator.impls.cproc.assembly.instructions;

import enshud.s4.compiler.generator.impls.cproc.assembly.operand.RegisterOperand;

public class LDD extends Instr1Op<LDD, RegisterOperand> {
    public LDD(RegisterOperand dest) {
        super(dest);
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
    public java.util.Optional<String> getBranchTarget() {
        return java.util.Optional.empty();
    }

    @Override
    public boolean isTerminal() {
        return false;
    }
}
