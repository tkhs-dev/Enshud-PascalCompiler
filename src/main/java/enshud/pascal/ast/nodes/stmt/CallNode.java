package enshud.pascal.ast.nodes.stmt;

import enshud.pascal.ast.IAstVisitor;
import enshud.pascal.ast.nodes.AstNode;
import enshud.pascal.ast.nodes.expr.Expr;

import java.util.List;

public class CallNode extends Stmt {
    public final String name;
    public final List<Expr> args;

    public CallNode(Position position, String name, List<Expr> args) {
        super(position);
        this.name = name;
        this.args = args;
    }

    @Override
    public <T> T accept(IAstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public List<AstNode> getChildren() {
        return List.copyOf(args);
    }
}
