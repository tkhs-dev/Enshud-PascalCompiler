package enshud.pascal.ast.nodes.expr;

import enshud.pascal.ast.IAstVisitor;
import enshud.pascal.ast.nodes.AstNode;

import java.util.List;

public class IndexedVarNode extends VarNode {
    public String name;
    public final Expr index;

    public IndexedVarNode(Position position, String name, Expr index) {
        super(position);
        this.name = name;
        this.index = index;
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
