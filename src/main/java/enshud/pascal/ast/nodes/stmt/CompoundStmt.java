package enshud.pascal.ast.nodes.stmt;

import enshud.pascal.ast.IAstVisitor;

import java.util.List;

public class CompoundStmt extends Stmt {
    public final List<Stmt> stmts;

    public CompoundStmt(Position position, List<Stmt> stmts) {
        super(position);
        this.stmts = stmts;
    }

    @Override
    public <T> T accept(IAstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
