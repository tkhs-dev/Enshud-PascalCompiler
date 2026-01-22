package enshud.s4.compiler.generator.impls.cproc.tir.instructions;

import enshud.s4.compiler.generator.impls.cproc.tir.operand.VirtualRegister;
import enshud.s4.compiler.instruction.OperandRef;

import java.util.Optional;
import java.util.Set;

public class Mov extends Instr2Op<Mov, VirtualRegister, VirtualRegister> {
    public Mov(VirtualRegister virtualRegister, VirtualRegister virtualRegister2) {
        super(virtualRegister, virtualRegister2);
    }

    public VirtualRegister getDest() {
        return op1.get();
    }

    public VirtualRegister getSrc() {
        return op2.get();
    }

    @Override
    public <R> R accept(ITIRVisitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override
    protected Set<OperandRef<?>> getUsedRefs() {
        return Set.of(op2);
    }

    @Override
    protected Set<OperandRef<?>> getDefinedRefs() {
        return Set.of(op1);
    }

    @Override
    public Set<VirtualRegister> getCopiedRegisters() {
        return Set.of(op1.get(), op2.get());
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
