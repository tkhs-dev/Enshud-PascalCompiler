package enshud.s4.compiler.generator.impls.casl.tir.instructions;

import enshud.s4.compiler.generator.AllocatableRegister;
import enshud.s4.compiler.generator.impls.casl.CaslRegisters;
import enshud.s4.compiler.generator.impls.casl.tir.operand.VirtualRegister;

import java.util.Set;

public class Epilogue extends TIrInstr<Epilogue> {
    private final OperandRef dummyGR6;
    private final OperandRef dummyGR7;

    public Epilogue() {
        this.dummyGR6 = new OperandRef(VirtualRegister.withPredefined("WRTGR6", CaslRegisters.GR6));
        this.dummyGR7 = new OperandRef(VirtualRegister.withPredefined("WRTGR7", CaslRegisters.GR7));
    }

    @Override
    public <R> R accept(ITIRVisitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Set<AllocatableRegister> getUse() {
        return registerSet(dummyGR6, dummyGR7);
    }

    @Override
    public Set<AllocatableRegister> getDef() {
        return Set.of();
    }

    @Override
    public void replaceUse(AllocatableRegister oldReg, AllocatableRegister newReg) {
        replaceAllocatable(oldReg, newReg, dummyGR6, dummyGR7);
    }

    @Override
    public void replaceDef(AllocatableRegister oldReg, AllocatableRegister newReg) {

    }
}
