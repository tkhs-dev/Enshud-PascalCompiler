package enshud.s4.compiler.generator.impls.casl.tir.instructions;

import enshud.s4.compiler.generator.AllocatableRegister;
import enshud.s4.compiler.generator.impls.casl.tir.operand.TIROperand;

import java.util.Arrays;
import java.util.Set;

public class Call extends TIrInstr<Call> {
    public final String functionName;
    private final OperandRef[] args;

    public Call(String functionName, TIROperand ... args) {
        this.args = args == null ? new OperandRef[0] : Arrays.stream(args).map(OperandRef::new).toArray(OperandRef[]::new);
        this.functionName = functionName;
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
        return registerSet(args);
    }

    @Override
    public Set<AllocatableRegister> getDef() {
        return Set.of();
    }

    @Override
    public void replaceUse(AllocatableRegister oldReg, AllocatableRegister newReg) {
        replaceAllocatable(oldReg, newReg, args);
    }

    @Override
    public void replaceDef(AllocatableRegister oldReg, AllocatableRegister newReg) {

    }

    @Override
    public boolean isCall() {
        return true;
    }
}
