package enshud.s4.compiler.generator.impls.cproc.assembly.instructions;

import enshud.s4.compiler.generator.impls.cproc.assembly.operand.RegisterOperand;

import java.util.Optional;

public abstract class OperationalInstr<T> extends Instr1Op<T, RegisterOperand>{
    public OperationalInstr(RegisterOperand dest) {
        super(dest);
    }

    public RegisterOperand getDest() {
        return op.get();
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
