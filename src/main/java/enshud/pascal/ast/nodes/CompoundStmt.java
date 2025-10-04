package enshud.pascal.ast.nodes;

import enshud.pascal.ast.AstVisitor;
import enshud.pascal.ast.nodes.stmt.Stmt;

import java.util.List;

public class CompoundStmt extends Stmt {
    public final List<Stmt> stmts;

    public CompoundStmt(Position position, List<Stmt> stmts) {
        super(position);
        this.stmts = stmts;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
