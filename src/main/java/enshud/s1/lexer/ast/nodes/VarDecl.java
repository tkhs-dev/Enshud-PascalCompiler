package enshud.s1.lexer.ast.nodes;

import enshud.s1.lexer.ast.AstVisitor;

import java.util.List;

public class VarDecl extends AstNode {
    public List<String> varNames;
    public TypeNode varType; // "int" or "char"

    public VarDecl(List<String> varNames, TypeNode varType) {
        this.varNames = varNames;
        this.varType = varType;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
