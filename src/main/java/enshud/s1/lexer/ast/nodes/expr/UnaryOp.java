package enshud.s1.lexer.ast.nodes.expr;

import enshud.s1.lexer.ast.AstVisitor;

public class UnaryOp extends Expr {
    public enum Op {
        PLUS, MINUS
    }

    public final Op op;
    public final Expr operand;

    public UnaryOp(Op op, Expr operand) {
        this.op = op;
        this.operand = operand;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
