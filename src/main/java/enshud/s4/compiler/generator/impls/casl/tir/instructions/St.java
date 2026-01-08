package enshud.s4.compiler.generator.impls.casl.tir.instructions;

import enshud.s4.compiler.generator.AllocatableRegister;
import enshud.s4.compiler.generator.impls.casl.tir.operand.TIROperand;

import java.util.Optional;
import java.util.Set;

public class St extends TIrInstr<St> {
    private final OperandRef src;
    private final OperandRef dest;

    public St(TIROperand src, TIROperand dest) {
        this.src = new OperandRef(src);
        this.dest = new OperandRef(dest);
    }

    public TIROperand getDest() {
        return dest.operand;
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
        return formatInstruction(src, dest);
    }

    @Override
    public Set<AllocatableRegister> getUse() {
        return registerSet(src);
    }

    @Override
    public Set<AllocatableRegister> getDef() {
        return registerSet(dest);
    }

    @Override
    public void replaceUse(AllocatableRegister oldReg, AllocatableRegister newReg) {
        replaceAllocatable(oldReg, newReg, src);
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
}
