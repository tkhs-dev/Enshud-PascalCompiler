package enshud.s4.compiler.generator.impls.casl.tir.instructions;

import enshud.s4.compiler.generator.AllocatableRegister;
import enshud.s4.compiler.generator.impls.casl.CaslRegisters;
import enshud.s4.compiler.generator.impls.casl.tir.operand.VirtualRegister;

import java.util.Optional;
import java.util.Set;

public class Prologue extends TIrInstr<Prologue> {
    private final OperandRef dummyGR6;
    private final OperandRef dummyGR7;

    public Prologue() {
        this.dummyGR6 = new OperandRef(VirtualRegister.withPredefined("WRTGR6", CaslRegisters.GR6).withSpillCost(0));
        this.dummyGR7 = new OperandRef(VirtualRegister.withPredefined("WRTGR7", CaslRegisters.GR7).withSpillCost(0));
    }

    @Override
    public <R> R accept(ITIRVisitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Set<AllocatableRegister> getUse() {
        return Set.of();
    }

    @Override
    public Set<AllocatableRegister> getDef() {
        return registerSet(dummyGR6, dummyGR7);
    }

    @Override
    public void replaceUse(AllocatableRegister oldReg, AllocatableRegister newReg) {

    }

    @Override
    public void replaceDef(AllocatableRegister oldReg, AllocatableRegister newReg) {
        replaceAllocatable(oldReg, newReg, dummyGR6, dummyGR7);
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
