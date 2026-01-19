package enshud.pascal.ast.nodes.stmt;

import enshud.pascal.ast.IAstVisitor;
import enshud.pascal.ast.nodes.AstNode;
import enshud.pascal.ast.nodes.expr.VarNode;

import java.util.List;

public class ReadNode extends Stmt {
    public final List<VarNode> variables;

    public ReadNode(Position position, List<VarNode> variables) {
        super(position);
        this.variables = variables;
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
