package enshud.pascal.ast.nodes;

import enshud.pascal.ast.IAstVisitor;

import java.util.List;

public class FormalParam extends AstNode {
    public final List<String> params;
    public final TypeNode type; // 型

    public FormalParam(Position position, List<String> params, TypeNode type) {
        super(position);
        this.params = params;
        this.type = type;
    }

    @Override
    public <T> T accept(IAstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
