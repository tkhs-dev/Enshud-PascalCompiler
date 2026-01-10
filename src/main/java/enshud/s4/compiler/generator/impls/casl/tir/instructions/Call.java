package enshud.s4.compiler.generator.impls.casl.tir.instructions;

import enshud.s4.compiler.generator.AllocatableRegister;
import enshud.s4.compiler.generator.impls.casl.tir.operand.TIROperand;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class Call extends TIrInstr<Call> {
    public final String functionName;
    public final boolean funcCall;
    private final OperandRef[] uses;
    private final OperandRef[] defs;

    public Call(String functionName, boolean funcCall, List<TIROperand> uses, List<TIROperand> defs) {
        this.functionName = functionName;
        this.funcCall = funcCall;
        this.uses = uses.stream().map(OperandRef::new).toArray(OperandRef[]::new);
        this.defs = defs.stream().map(OperandRef::new).toArray(OperandRef[]::new);
    }

    public Call(String functionName){
        this.functionName = functionName;
        this.funcCall = true;
        this.uses = new OperandRef[0];
        this.defs = new OperandRef[0];
    }

    @Override
    public <T> T accept(ITIRVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return formatInstruction(functionName);
    }

    @Override
    public Set<AllocatableRegister> getUse() {
        return registerSet(uses);
    }

    @Override
    public Set<AllocatableRegister> getDef() {
        return registerSet(defs);
    }

    @Override
    public void replaceUse(AllocatableRegister oldReg, AllocatableRegister newReg) {
        replaceAllocatable(oldReg, newReg, uses);
    }

    @Override
    public void replaceDef(AllocatableRegister oldReg, AllocatableRegister newReg) {
        replaceAllocatable(oldReg, newReg, defs);
    }

    @Override
    public boolean isCall() {
        return true;
    }

    @Override
    public Set<AllocatableRegister> getCopiedRegisters() {
        return Set.of();
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
