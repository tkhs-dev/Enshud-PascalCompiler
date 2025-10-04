package enshud.pascal.ast.nodes.stmt;

import enshud.pascal.ast.AstVisitor;
import enshud.pascal.ast.nodes.expr.Expr;

import java.util.List;

public class Call extends Stmt {
    public final String name;
    public final List<Expr> args;

    public Call(Position position, String name, List<Expr> args) {
        super(position);
        this.name = name;
        this.args = args;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
