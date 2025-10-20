package enshud.pascal.ast.nodes.stmt;

import enshud.pascal.ast.IAstVisitor;
import enshud.pascal.ast.nodes.AstNode;
import enshud.pascal.ast.nodes.expr.Expr;

import java.util.List;

public class IfNode extends Stmt {
    public final Expr condition;
    public final CompoundStmtNode thenBranch;
    public final CompoundStmtNode elseBranch; // can be null

    public IfNode(Position position, Expr condition, CompoundStmtNode thenBranch, CompoundStmtNode elseBranch) {
        super(position);
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    @Override
    public <T> T accept(IAstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public List<AstNode> getChildren() {
        if (elseBranch != null) {
            return List.of(condition, thenBranch, elseBranch);
        } else {
            return List.of(condition, thenBranch);
        }
    }
}
