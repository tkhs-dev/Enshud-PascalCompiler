package enshud.pascal.ast.nodes.stmt;

import enshud.pascal.ast.IAstVisitor;
import enshud.pascal.ast.nodes.expr.Expr;

import java.util.List;

public class Write extends Stmt {
    public final List<Expr> exprs;

    public Write(Position position, List<Expr> exprs) {
        super(position);
        this.exprs = exprs;
    }

    @Override
    public <T> T accept(IAstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
