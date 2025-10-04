package enshud.pascal.ast.nodes;

import enshud.pascal.ast.AstVisitor;

public class ArrayType extends TypeNode {
    public final int lowerBound;
    public final int upperBound;
    public final StandardType elementType;

    public ArrayType(Position position, int lowerBound, int upperBound, StandardType elementType) {
        super(position);
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.elementType = elementType;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
