package enshud.s4.compiler.generator.impls.casl.tir.instructions;

import enshud.s4.compiler.generator.AllocatableRegister;
import enshud.s4.compiler.generator.impls.casl.tir.operand.TIROperand;

import java.util.Optional;
import java.util.Set;

public class Ld extends TIrInstr<Ld> {
    private final OperandRef dest;
    private final OperandRef src;

    public Ld(TIROperand dest, TIROperand src) {
        this.dest = new OperandRef(dest);
        this.src = new OperandRef(src);
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
        return formatInstruction(dest, src);
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
