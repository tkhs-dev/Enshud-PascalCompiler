package enshud.s4.compiler.generator.impls.casl.tir.instructions;

import enshud.s4.compiler.generator.AllocatableRegister;
import enshud.s4.compiler.generator.impls.casl.tir.operand.TIROperand;

import java.util.Set;

public class Sub extends TIrInstr<Sub> {
    private final OperandRef left;
    private final OperandRef right;

    public Sub(TIROperand left, TIROperand right) {
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
}
