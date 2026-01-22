package enshud.s4.compiler.generator.impls.cproc.tir.instructions;

import enshud.s4.compiler.generator.impls.cproc.tir.operand.LabelOperand;
import enshud.s4.compiler.generator.impls.cproc.tir.operand.VirtualRegister;
import enshud.s4.compiler.instruction.OperandRef;

import java.util.Optional;
import java.util.Set;

public class Jmp extends Instr1Op<Jmp, LabelOperand>{
    private final Type type;

    public Jmp(LabelOperand target, Type type) {
        super(target);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public LabelOperand getTarget() {
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
        return Set.of();
    }

    @Override
    public Set<VirtualRegister> getCopiedRegisters() {
        return Set.of();
    }

    @Override
    public boolean isBranch() {
        return true;
    }

    @Override
    public boolean isConditionalBranch() {
        return type != Type.UNCONDITIONAL;
    }

    @Override
    public Optional<String> getBranchTarget() {
        return Optional.of(op.get().getLabel());
    }

    @Override
    public boolean isTerminal() {
        return false;
    }

    public enum Type {
        UNCONDITIONAL,
        ZERO,
        CARRY
    }
}
