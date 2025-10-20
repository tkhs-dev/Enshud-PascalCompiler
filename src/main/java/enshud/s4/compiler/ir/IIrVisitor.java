package enshud.s4.compiler.ir;

public interface IIrVisitor<T> {
    T visit(GetArgs ir);
    T visit(Decl ir);
    T visit(DeclArray ir);
    T visit(Jump ir);
    T visit(IfJump ir);
    T visit(Label ir);
    T visit(LoadIndexed ir);
    T visit(Move ir);
    T visit(StoreIndexed ir);
    T visit(IfNotJump ifNotJump);
    T visit(Return aReturn);
    T visit(Writeln writeln);
    T visit(SetArg setArg);
}
