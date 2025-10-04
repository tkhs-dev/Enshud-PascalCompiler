package enshud.pascal.ast.nodes.stmt;

import enshud.pascal.ast.AstVisitor;
import enshud.pascal.ast.nodes.CompoundStmt;
import enshud.pascal.ast.nodes.expr.Expr;

public class While extends Stmt {
    public final Expr condition;
    public final CompoundStmt body;

    public While(Position position, Expr condition, CompoundStmt body) {
        super(position);
        this.condition = condition;
        this.body = body;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
