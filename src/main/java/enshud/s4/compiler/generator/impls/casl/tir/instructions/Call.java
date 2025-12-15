package enshud.s4.compiler.generator.impls.casl.tir.instructions;

import enshud.s4.compiler.generator.AllocatableRegister;
import enshud.s4.compiler.generator.impls.casl.tir.operand.VirtualRegister;

import java.util.List;
import java.util.Set;

public class Call extends TIrInstr<Call> {
    public final String functionName;
    private static List<String> WRITE_CALLS = List.of("WRTINT", "WRTCH", "WRTLN", "WRTSTR");

    public Call(String functionName) {
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
        if (WRITE_CALLS.contains(functionName)) {
            return Set.of(new VirtualRegister("buf_pointer", 0), new VirtualRegister("buf", 0));
        }
        return Set.of();
    }

    @Override
    public Set<AllocatableRegister> getDef() {
        return Set.of();
    }

    @Override
    public boolean isCall() {
        return true;
    }
}
