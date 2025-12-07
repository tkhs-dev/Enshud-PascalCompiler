package enshud.pascal.ast.nodes;

import enshud.pascal.ast.IAstVisitor;
import enshud.pascal.type.Type;

import java.util.List;
import java.util.Optional;

public class VarDeclNode extends AstNode {
    public List<String> varNames;
    public TypeNode varType; // "int" or "char"

    private Type type;

    public VarDeclNode(Position position, List<String> varNames, TypeNode varType) {
        super(position);
        this.varNames = varNames;
        this.varType = varType;
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
        return List.of(varType);
    }
}
