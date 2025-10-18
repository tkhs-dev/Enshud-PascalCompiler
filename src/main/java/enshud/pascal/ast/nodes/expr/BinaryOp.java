package enshud.pascal.ast.nodes.expr;

import enshud.pascal.ast.IAstVisitor;
import enshud.pascal.ast.nodes.AstNode;

import java.util.List;

public class BinaryOp extends Expr {
    public enum Op {
        ADD, SUB, MUL, DIV, MOD,
        EQ, NEQ, LT, LTE, GT, GTE,
        AND, OR
    }

    public final Op op;
    public final Expr left;
    public final Expr right;

    public BinaryOp(Position position, Op op, Expr left, Expr right) {
        super(position);
        this.op = op;
        this.left = left;
        this.right = right;
    }

    @Override
    public <T> T accept(IAstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public List<AstNode> getChildren() {
        return List.of();
    }
}
