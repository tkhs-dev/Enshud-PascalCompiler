package enshud.s4.compiler.generator.impls.casl.tir.instructions;

public class Ret extends TIRInstruction {
    @Override
    public <T> T accept(ITIRVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
