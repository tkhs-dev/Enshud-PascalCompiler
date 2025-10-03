package enshud.s1.lexer.ast.nodes;

import enshud.s1.lexer.ast.AstVisitor;

public class StandardType extends TypeNode {
    public final String name;

    public StandardType(String name) {
        this.name = name;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
