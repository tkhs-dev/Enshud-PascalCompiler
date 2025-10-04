package enshud.pascal.ast.nodes.expr;

import enshud.pascal.ast.AstVisitor;

public class UnaryOp extends Expr {
    public enum Op {
        PLUS, MINUS, NOT
    }

    public final Op op;
    public final Expr operand;

    public UnaryOp(Position position, Op op, Expr operand) {
        super(position);
        this.op = op;
        this.operand = operand;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
