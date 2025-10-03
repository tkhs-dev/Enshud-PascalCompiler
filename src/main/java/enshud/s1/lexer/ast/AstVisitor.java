package enshud.s1.lexer.ast;

import enshud.s1.lexer.ast.nodes.expr.BinaryOp;
import enshud.s1.lexer.ast.nodes.expr.Const;
import enshud.s1.lexer.ast.nodes.expr.UnaryOp;
import enshud.s1.lexer.ast.nodes.expr.Var;

public interface AstVisitor<T> {
    T visit(BinaryOp node);
    T visit(Const node);
    T visit(UnaryOp node);
    T visit(Var node);
}
