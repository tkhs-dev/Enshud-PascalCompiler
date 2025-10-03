package enshud.s1.lexer.ast.nodes;

import enshud.s1.lexer.ast.AstVisitor;

import java.util.List;

public class FormalParam extends AstNode {
    public final List<String> params;
    public final TypeNode type; // 型

    public FormalParam(List<String> params, TypeNode type) {
        this.params = params;
        this.type = type;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
