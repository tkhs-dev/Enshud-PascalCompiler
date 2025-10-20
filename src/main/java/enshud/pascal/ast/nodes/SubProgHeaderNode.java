package enshud.pascal.ast.nodes;

import enshud.pascal.ast.IAstVisitor;

import java.util.List;

public class SubProgHeaderNode extends AstNode {
    public final String name; // サブルーチン名
    public final List<FormalParamNode> params;

    public SubProgHeaderNode(Position position, String name, List<FormalParamNode> params) {
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
        return List.copyOf(params);
    }
}
