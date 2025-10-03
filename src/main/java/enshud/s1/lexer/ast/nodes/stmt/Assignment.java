package enshud.s1.lexer.ast.nodes.stmt;

import enshud.s1.lexer.ast.AstVisitor;
import enshud.s1.lexer.ast.nodes.expr.Expr;

public class Assignment extends Stmt {
    public final String varName;
    public final Expr expr;

    public Assignment(String varName, Expr expr) {
        this.varName = varName;
        this.expr = expr;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
