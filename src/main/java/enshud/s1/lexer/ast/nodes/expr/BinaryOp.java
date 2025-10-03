package enshud.s1.lexer.ast.nodes.expr;

import enshud.s1.lexer.ast.AstVisitor;

public class BinaryOp extends Expr {
    public enum Op {
        ADD, SUB, MUL, DIV, MOD,
        EQ, NEQ, LT, LTE, GT, GTE,
        AND, OR
    }

    public final Op op;
    public final Expr left;
    public final Expr right;

    public BinaryOp(Op op, Expr left, Expr right) {
        this.op = op;
        this.left = left;
        this.right = right;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
