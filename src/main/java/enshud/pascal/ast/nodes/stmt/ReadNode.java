package enshud.pascal.ast.nodes.stmt;

import enshud.pascal.ast.IAstVisitor;
import enshud.pascal.ast.nodes.AstNode;

import java.util.List;

public class ReadNode extends Stmt {
    public final List<String> varNames;

    public ReadNode(Position position, List<String> varNames) {
        super(position);
        this.varNames = varNames;
    }

    @Override
    public <T> T accept(IAstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public List<AstNode> getChildren() {
        return List.of();
    }
}
