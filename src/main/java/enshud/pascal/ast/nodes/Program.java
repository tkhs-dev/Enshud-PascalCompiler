package enshud.pascal.ast.nodes;

import enshud.pascal.ast.AstVisitor;

public class Program extends AstNode {
    public final String name;
    public final Block block;
    public final CompoundStmt statement;

    public Program(Position position, String name, Block block, CompoundStmt statement) {
        super(position);
        this.name = name;
        this.block = block;
        this.statement = statement;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
