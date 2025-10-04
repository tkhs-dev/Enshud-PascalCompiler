package enshud.pascal.ast.nodes.stmt;

import enshud.pascal.ast.AstVisitor;
import enshud.pascal.ast.nodes.CompoundStmt;
import enshud.pascal.ast.nodes.expr.Expr;

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
