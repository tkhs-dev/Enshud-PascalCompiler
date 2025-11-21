package enshud.s4.compiler.generator.impls.casl.tir.instructions;

public interface ITIRVisitor<T> {
    T visit(Add ir);
    T visit(And ir);
    T visit(Call ir);
    T visit(Cp ir);
    T visit(Jmp ir);
    T visit(Lad ir);
    T visit(Ld ir);
    T visit(Nop ir);
    T visit(Or ir);
    T visit(Pop ir);
    T visit(Push ir);
    T visit(Ret ir);
    T visit(Sl ir);
    T visit(Sr ir);
    T visit(St ir);
    T visit(Sub ir);
    T visit(Xor ir);
}
