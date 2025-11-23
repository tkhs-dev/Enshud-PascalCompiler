package enshud.s4.compiler.generator.impls.casl.tir.instructions;

public class Call extends TIRInstruction<Call> {
    public final String functionName;

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
}
