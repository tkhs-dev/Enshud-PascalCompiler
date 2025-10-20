package enshud.pascal.ast.nodes.stmt;

import enshud.pascal.ast.IAstVisitor;
import enshud.pascal.ast.nodes.AstNode;

import java.util.List;

public class CompoundStmtNode extends Stmt {
    public final List<Stmt> stmts;

    public CompoundStmtNode(Position position, List<Stmt> stmts) {
        super(position);
        this.stmts = stmts;
    }

    @Override
    public <T> T accept(IAstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public List<AstNode> getChildren() {
        return List.copyOf(stmts);
    }
}
