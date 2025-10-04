package enshud.pascal.ast.nodes.stmt;

import enshud.pascal.ast.AstVisitor;
import enshud.pascal.ast.nodes.expr.Expr;
import enshud.pascal.ast.nodes.expr.VarNode;

public class Assignment extends Stmt {
    public final VarNode var;
    public final Expr expr;

    public Assignment(VarNode var, Expr expr) {
        this.var = var;
        this.expr = expr;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
