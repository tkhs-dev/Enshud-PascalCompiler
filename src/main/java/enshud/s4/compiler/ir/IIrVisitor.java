package enshud.s4.compiler.ir;

import enshud.s4.compiler.ir.instructions.*;

public interface IIrVisitor<T> {
    T visit(Call ir);
    T visit(Decl ir);
    T visit(DeclArray ir);
    T visit(Jump ir);
    T visit(Label ir);
    T visit(LoadIndexed ir);
    T visit(Move ir);
    T visit(StoreIndexed ir);
    T visit(Writeln writeln);
    T visit(BinaryOp binaryOp);
    T visit(Load load);
    T visit(Store store);
}
