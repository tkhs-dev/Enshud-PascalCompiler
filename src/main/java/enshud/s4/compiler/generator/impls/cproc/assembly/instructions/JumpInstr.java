package enshud.s4.compiler.generator.impls.cproc.assembly.instructions;

import enshud.s4.compiler.generator.impls.cproc.assembly.operand.LabelOperand;

import java.util.Optional;

public abstract class JumpInstr<T> extends Instr1Op<T, LabelOperand> {
    public JumpInstr(LabelOperand target) {
        super(target);
    }

    @Override
    public boolean isBranch() {
        return true;
    }

    @Override
    public Optional<String> getBranchTarget() {
        return Optional.of(op.get().getLabel());
    }

    @Override
    public boolean isTerminal() {
        return false;
    }
}
