package enshud.s1.lexer.ast;

import enshud.s1.lexer.ast.nodes.expr.BinaryOp;

public interface AstVisitor<T> {
    T visit(BinaryOp node);

}
