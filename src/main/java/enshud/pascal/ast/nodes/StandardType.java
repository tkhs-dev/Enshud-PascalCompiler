package enshud.pascal.ast.nodes;

import enshud.pascal.ast.AstVisitor;

public class StandardType extends TypeNode {
    public final String name;

    public StandardType(Position position, String name) {
        super(position);
        this.name = name;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
