package enshud.pascal.ast.nodes;

import enshud.pascal.ast.IAstVisitor;

import java.util.List;

public class SubProgHeader extends AstNode {
    public final String name; // サブルーチン名
    public final List<FormalParam> params;

    public SubProgHeader(Position position, String name, List<FormalParam> params) {
        super(position);
        this.name = name;
        this.params = params;
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
