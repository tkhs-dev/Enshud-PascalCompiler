package enshud.pascal.ast.nodes.stmt;

import enshud.pascal.ast.IAstVisitor;
import enshud.pascal.ast.nodes.AstNode;
import enshud.pascal.ast.nodes.expr.Expr;

import java.util.List;

public class While extends Stmt {
    public final Expr condition;
    public final CompoundStmt body;

    public While(Position position, Expr condition, CompoundStmt body) {
        super(position);
        this.condition = condition;
        this.body = body;
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
