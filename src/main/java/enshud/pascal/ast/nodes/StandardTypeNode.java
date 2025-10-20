package enshud.pascal.ast.nodes;

import enshud.pascal.ast.IAstVisitor;

import java.util.List;

public class StandardTypeNode extends TypeNode {
    public final String name;

    public StandardTypeNode(Position position, String name) {
        super(position);
        this.name = name;
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
