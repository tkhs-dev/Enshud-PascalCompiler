package enshud.s1.lexer.ast.nodes;

import enshud.s1.lexer.ast.AstVisitor;

// 抽象構文木ノード
public abstract class AstNode {
    public abstract <T> T accept(AstVisitor<T> visitor);
}

