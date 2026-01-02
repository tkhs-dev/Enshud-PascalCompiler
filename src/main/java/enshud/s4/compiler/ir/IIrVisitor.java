package enshud.s4.compiler.ir;

import enshud.s4.compiler.ir.instructions.*;

public interface IIrVisitor<T> {
    T visit(Call ir);
    T visit(Jump ir);
    T visit(Label ir);
    T visit(LoadIndexed ir);
    T visit(Move ir);
    T visit(StoreIndexed ir);
    T visit(Write ir);
    T visit(BinaryOp ir);
    T visit(UnaryOp ir);
    T visit(Load ir);
    T visit(Store ir);
}
