package enshud.pascal.ast.nodes;

import enshud.pascal.ast.IAstVisitor;

import java.util.List;

public class ArrayTypeNode extends TypeNode {
    public final int lowerBound;
    public final int upperBound;
    public final StandardTypeNode elementType;

    public ArrayTypeNode(Position position, int lowerBound, int upperBound, StandardTypeNode elementType) {
        super(position);
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.elementType = elementType;
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
