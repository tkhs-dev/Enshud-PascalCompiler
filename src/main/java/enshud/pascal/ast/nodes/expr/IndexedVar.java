package enshud.pascal.ast.nodes.expr;

import enshud.pascal.ast.AstVisitor;

public class IndexedVar extends VarNode {
    public final String name;
    public final Expr index;

    public IndexedVar(Position position, String name, Expr index) {
        super(position);
        this.name = name;
        this.index = index;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
