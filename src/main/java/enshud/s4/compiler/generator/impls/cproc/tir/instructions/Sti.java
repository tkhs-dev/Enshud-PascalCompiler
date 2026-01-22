package enshud.s4.compiler.generator.impls.cproc.tir.instructions;

import enshud.s4.compiler.generator.impls.cproc.tir.operand.ImmediateOperand;
import enshud.s4.compiler.generator.impls.cproc.tir.operand.LabelOperand;
import enshud.s4.compiler.generator.impls.cproc.tir.operand.VirtualRegister;
import enshud.s4.compiler.instruction.OperandRef;

import java.util.Optional;
import java.util.Set;

public class Sti extends Instr2Op<Sti, ImmediateOperand, LabelOperand> {
    public Sti(ImmediateOperand immediateOperand, LabelOperand labelOperand) {
        super(immediateOperand, labelOperand);
    }

    public ImmediateOperand getValue() {
        return op1.get();
    }

    public LabelOperand getDest() {
        return op2.get();
    }

    @Override
    public <R> R accept(ITIRVisitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override
    protected Set<OperandRef<?>> getUsedRefs() {
        return Set.of();
    }

    @Override
    protected Set<OperandRef<?>> getDefinedRefs() {
        return Set.of();
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
}
