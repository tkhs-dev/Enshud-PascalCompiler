package enshud.pascal.ast.nodes.stmt;

import enshud.pascal.ast.IAstVisitor;
import enshud.pascal.ast.nodes.AstNode;
import enshud.pascal.ast.nodes.expr.Expr;

import java.util.List;

public class WriteNode extends Stmt {
    public final List<Expr> exprs;

    public WriteNode(Position position, List<Expr> exprs) {
        super(position);
        this.exprs = exprs;
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
