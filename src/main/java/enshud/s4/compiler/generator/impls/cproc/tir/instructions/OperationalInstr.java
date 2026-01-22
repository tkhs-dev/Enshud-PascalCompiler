package enshud.s4.compiler.generator.impls.cproc.tir.instructions;

import enshud.s4.compiler.generator.impls.cproc.tir.operand.VirtualRegister;
import enshud.s4.compiler.instruction.OperandRef;

import java.util.Optional;
import java.util.Set;

public abstract class OperationalInstr<T extends TIRInstr<?>> extends Instr2Op<T, VirtualRegister, VirtualRegister>{
    public OperationalInstr(VirtualRegister left, VirtualRegister right) {
        super(left, right);
    }

    public VirtualRegister getLeft() {
        return op1.get();
    }

    public VirtualRegister getRight() {
        return op2.get();
    }

    @Override
    public Set<VirtualRegister> getCopiedRegisters() {
        return Set.of();
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
    protected Set<OperandRef<?>> getUsedRefs() {
        return Set.of(op1, op2);
    }

    @Override
    protected Set<OperandRef<?>> getDefinedRefs() {
        return Set.of(op1);
    }
}
