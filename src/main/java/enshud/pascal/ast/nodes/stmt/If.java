package enshud.pascal.ast.nodes.stmt;

import enshud.pascal.ast.IAstVisitor;
import enshud.pascal.ast.nodes.expr.Expr;

public class If extends Stmt {
    public final Expr condition;
    public final CompoundStmt thenBranch;
    public final CompoundStmt elseBranch; // can be null

    public If(Position position, Expr condition, CompoundStmt thenBranch, CompoundStmt elseBranch) {
        super(position);
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    @Override
    public <T> T accept(IAstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
