package enshud.s4.compiler.generator.impls.casl.tir.instructions;

import enshud.s4.compiler.generator.AllocatableRegister;
import enshud.s4.compiler.generator.impls.casl.tir.operand.TIROperand;

import java.util.Set;

public class Pop extends TIrInstr<Pop> {
    private final OperandRef target;

    public Pop(TIROperand target) {
        this.target = new OperandRef(target);
    }

    public TIROperand getTarget() {
        return target.operand;
    }

    @Override
    public <T> T accept(ITIRVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return formatInstruction(target);
    }

    @Override
    public Set<AllocatableRegister> getUse() {
        return registerSet();
    }

    @Override
    public Set<AllocatableRegister> getDef() {
        return registerSet(target);
    }

    @Override
    public void replaceUse(AllocatableRegister oldReg, AllocatableRegister newReg) {

    }

    @Override
    public void replaceDef(AllocatableRegister oldReg, AllocatableRegister newReg) {
        replaceAllocatable(oldReg, newReg, target);
    }
}
