package enshud.pascal.ast.nodes;

import enshud.pascal.ast.AstVisitor;

// 抽象構文木ノード
public abstract class AstNode {
    public abstract <T> T accept(AstVisitor<T> visitor);
}

