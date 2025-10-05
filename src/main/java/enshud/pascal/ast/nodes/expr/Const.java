package enshud.pascal.ast.nodes.expr;

import enshud.pascal.ast.IAstVisitor;
import enshud.pascal.type.Type;

public class Const extends Expr {
    public final Object value;

    public Const(Position position, Object value) {
        super(position);
        this.value = value;
    }

    @Override
    public <T> T accept(IAstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
