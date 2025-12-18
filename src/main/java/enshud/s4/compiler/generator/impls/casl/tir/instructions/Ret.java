package enshud.s4.compiler.generator.impls.casl.tir.instructions;

import enshud.s4.compiler.generator.AllocatableRegister;

import java.util.Set;

public class Ret extends TIrInstr<Ret> {
    @Override
    public <T> T accept(ITIRVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Set<AllocatableRegister> getUse() {
        return Set.of();
    }

    @Override
    public Set<AllocatableRegister> getDef() {
        return Set.of();
    }

    @Override
    public void replaceUse(AllocatableRegister oldReg, AllocatableRegister newReg) {

    }

    @Override
    public void replaceDef(AllocatableRegister oldReg, AllocatableRegister newReg) {

    }
}
