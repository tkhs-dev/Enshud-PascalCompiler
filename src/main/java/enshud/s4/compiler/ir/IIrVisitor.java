package enshud.s4.compiler.ir;

public interface IIrVisitor<T> {
    T visit(BinaryOp ir);
    T visit(Decl ir);
    T visit(DeclArray ir);
    T visit(Goto ir);
    T visit(IfGoto ir);
    T visit(Label ir);
    T visit(LoadIndexed ir);
    T visit(Move ir);
    T visit(StoreIndexed ir);
}
