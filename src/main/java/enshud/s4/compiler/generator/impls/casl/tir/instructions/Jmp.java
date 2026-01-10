package enshud.s4.compiler.generator.impls.casl.tir.instructions;

import enshud.s4.compiler.generator.AllocatableRegister;

import java.util.Optional;
import java.util.Set;

public class Jmp extends TIrInstr<Jmp> {
    public final String addressLabel;
    public final Type type;

    public Jmp(String addressLabel, Type type) {
        this.addressLabel = addressLabel;
        this.type = type;
    }

    @Override
    public <T> T accept(ITIRVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return formatInstruction(addressLabel, type);
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

    @Override
    public boolean isBranch() {
        return true;
    }

    @Override
    public boolean isConditionalBranch() {
        return type != Type.UNCONDITIONAL;
    }

    @Override
    public Optional<String> getBranchTarget() {
        return Optional.of(addressLabel);
    }

    @Override
    public boolean isTerminal() {
        return false;
    }

    @Override
    public Set<AllocatableRegister> getCopiedRegisters() {
        return Set.of();
    }


    public enum Type {
        PLUS,
        MINUS,
        ZERO,
        NONZERO,
        OVERFLOW,
        UNCONDITIONAL
    }
}
