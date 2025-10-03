package enshud.s1.lexer.ast.nodes;

import enshud.s1.lexer.ast.AstVisitor;

public class Program extends AstNode {
    public final String name;
    public final Block block;
    public final CompoundStmt statement;

    public Program(String name, Block block, CompoundStmt statement) {
        this.name = name;
        this.block = block;
        this.statement = statement;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
