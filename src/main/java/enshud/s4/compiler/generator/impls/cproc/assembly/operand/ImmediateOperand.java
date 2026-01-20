package enshud.s4.compiler.generator.impls.cproc.assembly.operand;

public class ImmediateOperand extends AssemblyOperand{
    private final int value;

    public ImmediateOperand(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("%d", value);
    }
}
