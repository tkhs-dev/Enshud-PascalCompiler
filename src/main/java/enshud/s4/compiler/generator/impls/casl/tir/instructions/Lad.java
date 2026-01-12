package enshud.s4.compiler.generator.impls.casl.tir.instructions;

import enshud.s4.compiler.generator.AllocatableRegister;
import enshud.s4.compiler.generator.impls.casl.tir.operand.TIROperand;

import java.util.Optional;
import java.util.Set;

public class Lad extends TIrInstr<Lad> {
    private final OperandRef dest;
    private final OperandRef value;

    public Lad(TIROperand dest, TIROperand value) {
        this.dest = new OperandRef(dest);
        this.value = new OperandRef(value);
    }

    public TIROperand getDest() {
        return dest.get();
    }

    public TIROperand getValue() {
        return value.get();
    }

    @Override
    public <T> T accept(ITIRVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return formatInstruction(dest, value);
    }

    @Override
    public Set<AllocatableRegister> getUse() {
        return registerSet(value);
    }

    @Override
    public Set<AllocatableRegister> getDef() {
        return registerSet(dest);
    }

    @Override
    public void replaceUse(AllocatableRegister oldReg, AllocatableRegister newReg) {
        replaceAllocatable(oldReg, newReg, value);
    }

    @Override
    public void replaceDef(AllocatableRegister oldReg, AllocatableRegister newReg) {
        replaceAllocatable(oldReg, newReg, dest);
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
