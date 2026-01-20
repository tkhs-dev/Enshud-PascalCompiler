package enshud.s4.compiler.generator.impls.cproc.assembly.instructions;

import enshud.s4.compiler.generator.impls.cproc.assembly.operand.ImmediateOperand;

import java.util.Optional;

public class STI extends Instr1Op<STI, ImmediateOperand>{
    public STI(ImmediateOperand value) {
        super(value);
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
