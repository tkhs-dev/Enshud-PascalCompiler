package enshud.pascal.ast.nodes.stmt;

import enshud.pascal.ast.AstVisitor;
import enshud.pascal.ast.nodes.expr.Expr;

import java.util.List;

public class Write extends Stmt {
    public final List<Expr> exprs;

    public Write(List<Expr> exprs) {
        this.exprs = exprs;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
