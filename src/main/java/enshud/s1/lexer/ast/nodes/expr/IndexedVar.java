package enshud.s1.lexer.ast.nodes.expr;

import enshud.s1.lexer.ast.AstVisitor;

public class IndexedVar extends VarNode {
    public final String name;
    public final Expr index;

    public IndexedVar(String name, Expr index) {
        this.name = name;
        this.index = index;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
