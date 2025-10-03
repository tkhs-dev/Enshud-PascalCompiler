package enshud.s1.lexer.ast.nodes.stmt;

import enshud.s1.lexer.ast.AstVisitor;
import enshud.s1.lexer.ast.nodes.expr.Expr;

import java.util.List;

public class Call extends Stmt {
    public final String name;
    public final List<Expr> args;

    public Call(String name, List<Expr> args) {
        this.name = name;
        this.args = args;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
