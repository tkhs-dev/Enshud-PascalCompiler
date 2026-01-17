package enshud.s4.compiler.generator.impls.casl.tir.instructions;

import enshud.s4.compiler.generator.AllocatableRegister;
import enshud.s4.compiler.generator.impls.casl.tir.operand.FrameSlot;
import enshud.s4.compiler.generator.impls.casl.tir.operand.TIROperand;
import enshud.s4.compiler.generator.impls.casl.tir.operand.VirtualRegister;
import enshud.s4.compiler.instruction.OperandRef;

import java.util.Optional;
import java.util.Set;

public class St extends Instr2Op<St, VirtualRegister, FrameSlot> {
    public St(VirtualRegister src, FrameSlot dest) {
        super(src, dest);
    }

    public FrameSlot getDest() {
        return op2.get();
    }

    public VirtualRegister getSrc() {
        return op1.get();
    }

    @Override
    public <T> T accept(ITIRVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    protected Set<OperandRef<?>> getUsedRefs() {
        return Set.of(op1, op2);
    }

    @Override
    protected Set<OperandRef<?>> getDefinedRefs() {
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
    public Set<VirtualRegister> getCopiedRegisters() {
        return Set.of();
    }
}
