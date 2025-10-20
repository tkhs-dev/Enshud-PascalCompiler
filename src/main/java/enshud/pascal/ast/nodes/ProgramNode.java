package enshud.pascal.ast.nodes;

import enshud.pascal.ast.IAstVisitor;
import enshud.pascal.ast.nodes.stmt.CompoundStmtNode;

import java.util.List;

public class ProgramNode extends AstNode {
    public final String name;
    public final BlockNode block;
    public final CompoundStmtNode statement;

    public ProgramNode(Position position, String name, BlockNode block, CompoundStmtNode statement) {
        super(position);
        this.name = name;
        this.block = block;
        this.statement = statement;
    }

    @Override
    public <T> T accept(IAstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public List<AstNode> getChildren() {
        return List.of(block, statement);
    }
}
