package enshud.pascal.ast.nodes.expr;

import enshud.pascal.ast.AstVisitor;

public class Const extends Expr {
    public final Object value;

    public Const(Object value) {
        this.value = value;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
