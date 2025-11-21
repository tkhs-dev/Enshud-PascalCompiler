package enshud.s4.compiler.generator.impls.casl.tir.instructions;

public abstract class TIRInstruction {
    public abstract <T> T accept(ITIRVisitor<T> visitor);
}
