package enshud.s4.compiler.generator.impls.cproc.tir.instructions;

public interface ITIRVisitor<T> {
    T visit(Add ir);
    T visit(And ir);
    T visit(Cmp ir);
    T visit(Dec ir);
    T visit(End ir);
    T visit(Inc ir);
    T visit(Jmp ir);
    T visit(Ldd ir);
    T visit(Ldi ir);
    T visit(Mov ir);
    T visit(Mul ir);
    T visit(Nop ir);
    T visit(Not ir);
    T visit(Or ir);
    T visit(Std ir);
    T visit(Sti ir);
    T visit(Sub ir);
}
