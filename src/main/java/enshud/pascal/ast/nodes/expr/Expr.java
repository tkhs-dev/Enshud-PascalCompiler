package enshud.pascal.ast.nodes.expr;

import enshud.pascal.ast.nodes.AstNode;

import java.util.List;

// Expression
public abstract class Expr extends AstNode {

    public Expr(Position position) {
        super(position);
    }

    @Override
    public List<AstNode> getChildren() {
        return List.of();
    }
}
