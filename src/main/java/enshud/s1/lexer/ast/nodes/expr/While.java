package enshud.s1.lexer.ast.nodes.expr;

import enshud.s1.lexer.ast.AstVisitor;
import enshud.s1.lexer.ast.nodes.CompoundStmt;
import enshud.s1.lexer.ast.nodes.stmt.Stmt;

public class While extends Stmt {
    public final Expr condition;
    public final CompoundStmt body;

    public While(Expr condition, CompoundStmt body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
