package enshud.pascal.ast.nodes.stmt;

import enshud.pascal.ast.nodes.AstNode;

// Statement
public abstract class Stmt extends AstNode {

    public Stmt(Position position) {
        super(position);
    }
}
