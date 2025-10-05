package enshud.pascal.ast.nodes.stmt;

import enshud.pascal.ast.IAstVisitor;
import enshud.pascal.ast.nodes.expr.Expr;
import enshud.pascal.ast.nodes.expr.VarNode;

public class Assignment extends Stmt {
    public final VarNode var;
    public final Expr expr;

    public Assignment(Position position, VarNode var, Expr expr) {
        super(position);
        this.var = var;
        this.expr = expr;
    }

    @Override
    public <T> T accept(IAstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
