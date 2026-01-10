package enshud.s4.compiler.generator.impls.casl.tir.instructions;

import enshud.s4.compiler.generator.AllocatableRegister;
import enshud.s4.compiler.generator.impls.casl.tir.operand.TIROperand;

import java.util.Optional;
import java.util.Set;

public class Push extends TIrInstr<Push> {
    private final OperandRef src;

    public Push(TIROperand src) {
        this.src = new OperandRef(src);
    }

    public TIROperand getSrc() {
        return src.operand;
    }

    @Override
    public <T> T accept(ITIRVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return formatInstruction(src);
    }

    @Override
    public Set<AllocatableRegister> getUse() {
        return registerSet(src);
    }

    @Override
    public Set<AllocatableRegister> getDef() {
        return registerSet();
    }

    @Override
    public void replaceUse(AllocatableRegister oldReg, AllocatableRegister newReg) {
        replaceAllocatable(oldReg, newReg, src);
    }

    @Override
    public void replaceDef(AllocatableRegister oldReg, AllocatableRegister newReg) {

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
    public Set<AllocatableRegister> getCopiedRegisters() {
        return Set.of();
    }
}
