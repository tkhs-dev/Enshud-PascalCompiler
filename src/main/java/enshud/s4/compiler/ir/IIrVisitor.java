package enshud.s4.compiler.ir;

public interface IIrVisitor<T> {
    T visit(Call ir);
    T visit(Decl ir);
    T visit(DeclArray ir);
    T visit(Jump ir);
    T visit(Label ir);
    T visit(LoadIndexed ir);
    T visit(Move ir);
    T visit(StoreIndexed ir);
    T visit(Return aReturn);
    T visit(Writeln writeln);
    T visit(BinaryOp binaryOp);
    T visit(Labeled labeled);
    T visit(GetArgument getArgument);
    T visit(Load load);
    T visit(Store store);
}
