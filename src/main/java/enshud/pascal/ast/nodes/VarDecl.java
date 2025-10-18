package enshud.pascal.ast.nodes;

import enshud.pascal.ast.IAstVisitor;

import java.util.List;

public class VarDecl extends AstNode {
    public List<String> varNames;
    public TypeNode varType; // "int" or "char"

    public VarDecl(Position position, List<String> varNames, TypeNode varType) {
        super(position);
        this.varNames = varNames;
        this.varType = varType;
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
