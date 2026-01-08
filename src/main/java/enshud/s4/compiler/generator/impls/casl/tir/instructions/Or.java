package enshud.s4.compiler.generator.impls.casl.tir.instructions;

import enshud.s4.compiler.generator.AllocatableRegister;
import enshud.s4.compiler.generator.impls.casl.tir.operand.TIROperand;

import java.util.Optional;
import java.util.Set;

public class Or extends TIrInstr<Or> {
    private final OperandRef left;
    private final OperandRef right;

    public Or(TIROperand left, TIROperand right) {
        this.left = new OperandRef(left);
        this.right = new OperandRef(right);
    }

    public TIROperand getLeft() {
        return left.operand;
    }

    public TIROperand getRight() {
        return right.operand;
    }

    @Override
    public <T> T accept(ITIRVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return formatInstruction(left, right);
    }

    @Override
    public Set<AllocatableRegister> getUse() {
        return registerSet(left, right);
    }

    @Override
    public Set<AllocatableRegister> getDef() {
        return registerSet(left);
    }

    @Override
    public void replaceUse(AllocatableRegister oldReg, AllocatableRegister newReg) {
        replaceAllocatable(oldReg, newReg, left, right);
    }

    @Override
    public void replaceDef(AllocatableRegister oldReg, AllocatableRegister newReg) {
        replaceAllocatable(oldReg, newReg, left);
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
