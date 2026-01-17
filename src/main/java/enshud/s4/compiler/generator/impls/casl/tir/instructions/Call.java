package enshud.s4.compiler.generator.impls.casl.tir.instructions;

import enshud.s4.compiler.generator.AllocatableRegister;
import enshud.s4.compiler.generator.impls.casl.tir.operand.TIROperand;
import enshud.s4.compiler.generator.impls.casl.tir.operand.VirtualRegister;
import enshud.s4.compiler.instruction.OperandRef;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class Call extends TIrInstr<Call> {
    public final String functionName;
    public final boolean funcCall;
    private final List<OperandRef<TIROperand>> uses;
    private final List<OperandRef<TIROperand>> defs;

    public Call(String functionName, boolean funcCall, List<TIROperand> uses, List<TIROperand> defs) {
        this.functionName = functionName;
        this.funcCall = funcCall;
        this.uses = uses.stream().map(OperandRef::new).toList();
        this.defs = defs.stream().map(OperandRef::new).toList();
    }

    public Call(String functionName){
        this.functionName = functionName;
        this.funcCall = true;
        this.uses = List.of();
        this.defs = List.of();
    }

    @Override
    public <T> T accept(ITIRVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Set<? extends TIROperand> getOperands() {
        return Set.copyOf(
                Arrays.asList(
                        Stream.concat(uses.stream(), defs.stream())
                                .map(OperandRef::get)
                                .toArray(TIROperand[]::new)
                )
        );
    }

    @Override
    protected Set<OperandRef<?>> getUsedRefs() {
        return Set.copyOf(uses);
    }

    @Override
    protected Set<OperandRef<?>> getDefinedRefs() {
        return Set.copyOf(defs);
    }

    @Override
    public String toString() {
        return formatInstruction(functionName);
    }

    @Override
    public boolean isCall() {
        return true;
    }

    @Override
    public Set<VirtualRegister> getCopiedRegisters() {
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
