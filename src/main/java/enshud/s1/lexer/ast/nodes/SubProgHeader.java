package enshud.s1.lexer.ast.nodes;

import enshud.s1.lexer.ast.AstVisitor;

import java.util.List;

public class SubProgHeader extends AstNode {
    public final String name; // サブルーチン名
    public final List<FormalParam> params;

    public SubProgHeader(String name, List<FormalParam> params) {
        this.name = name;
        this.params = params;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
