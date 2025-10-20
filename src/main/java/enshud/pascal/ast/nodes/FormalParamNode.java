package enshud.pascal.ast.nodes;

import enshud.pascal.ast.IAstVisitor;

import java.util.List;

public class FormalParamNode extends AstNode {
    public final List<String> params;
    public final TypeNode type; // 型

    public FormalParamNode(Position position, List<String> params, TypeNode type) {
        super(position);
        this.params = params;
        this.type = type;
    }

    @Override
    public <T> T accept(IAstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public List<AstNode> getChildren() {
        return List.of(type);
    }
}
