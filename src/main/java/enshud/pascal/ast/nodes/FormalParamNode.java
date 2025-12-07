package enshud.pascal.ast.nodes;

import enshud.pascal.ast.IAstVisitor;
import enshud.pascal.type.Type;

import java.util.List;
import java.util.Optional;

public class FormalParamNode extends AstNode {
    public List<String> params;
    public final TypeNode typeNode; // 型

    private Type type;

    public FormalParamNode(Position position, List<String> params, TypeNode typeNode) {
        super(position);
        this.params = params;
        this.typeNode = typeNode;
        this.type = null;
    }

    public Optional<Type> getType() {
        return Optional.ofNullable(type);
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public <T> T accept(IAstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public List<AstNode> getChildren() {
        return List.of(typeNode);
    }
}
