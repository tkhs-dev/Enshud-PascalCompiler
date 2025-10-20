package enshud.pascal.ast.nodes.expr;

import enshud.pascal.ast.IAstVisitor;
import enshud.pascal.ast.nodes.AstNode;

import java.util.List;

public class StandardVarNode extends VarNode {
    public final String name;

    public StandardVarNode(Position position, String name) {
        super(position);
        this.name = name;
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
