package enshud.s1.lexer.ast.nodes.stmt;

import enshud.s1.lexer.ast.AstVisitor;
import enshud.s1.lexer.ast.nodes.expr.Expr;

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
