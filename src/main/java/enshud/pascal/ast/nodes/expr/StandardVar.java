package enshud.pascal.ast.nodes.expr;

import enshud.pascal.ast.AstVisitor;

public class StandardVar extends VarNode {
    public final String name;

    public StandardVar(Position position, String name) {
        super(position);
        this.name = name;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
