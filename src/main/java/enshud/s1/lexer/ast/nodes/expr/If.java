package enshud.s1.lexer.ast.nodes.expr;

import enshud.s1.lexer.ast.AstVisitor;
import enshud.s1.lexer.ast.nodes.CompoundStmt;
import enshud.s1.lexer.ast.nodes.stmt.Stmt;

public class If extends Stmt {
    public final Expr condition;
    public final CompoundStmt thenBranch;
    public final CompoundStmt elseBranch; // can be null

    public If(Expr condition, CompoundStmt thenBranch, CompoundStmt elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
