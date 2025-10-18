package enshud.pascal.ast.nodes.stmt;

import enshud.pascal.ast.nodes.AstNode;

import java.util.List;

// Statement
public abstract class Stmt extends AstNode {

    public Stmt(Position position) {
        super(position);
    }

    @Override
    public List<AstNode> getChildren() {
        return List.of();
    }
}
