package enshud.s1.lexer.ast.nodes.expr;

import enshud.s1.lexer.ast.AstVisitor;

public class Var extends Expr {
    public final String name;

    public Var(String name) {
        this.name = name;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
