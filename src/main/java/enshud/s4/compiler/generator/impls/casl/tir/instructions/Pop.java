package enshud.s4.compiler.generator.impls.casl.tir.instructions;

import enshud.s4.compiler.generator.AllocatableRegister;
import enshud.s4.compiler.generator.impls.casl.tir.operand.TIROperand;
import enshud.s4.compiler.generator.impls.casl.tir.operand.VirtualRegister;
import enshud.s4.compiler.instruction.OperandRef;

import java.util.Optional;
import java.util.Set;

public class Pop extends Instr1Op<Pop, VirtualRegister> {
    public Pop(VirtualRegister target) {
        super(target);
    }

    public VirtualRegister getTarget() {
        return op.get();
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
        return Set.of(op);
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
