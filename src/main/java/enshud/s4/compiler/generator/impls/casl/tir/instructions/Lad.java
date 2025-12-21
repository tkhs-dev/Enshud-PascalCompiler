package enshud.s4.compiler.generator.impls.casl.tir.instructions;

import enshud.s4.compiler.generator.AllocatableRegister;
import enshud.s4.compiler.generator.impls.casl.tir.operand.TIROperand;

import java.util.Set;

public class Lad extends TIrInstr<Lad> {
    private final OperandRef dest;
    private final OperandRef value;

    public Lad(TIROperand dest, TIROperand value) {
        this.dest = new OperandRef(dest);
        this.value = new OperandRef(value);
    }

    public TIROperand getDest() {
        return dest.operand;
    }

    public TIROperand getValue() {
        return value.operand;
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
}
