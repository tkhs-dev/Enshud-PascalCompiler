package enshud.pascal.ast.nodes.stmt;

import enshud.pascal.ast.IAstVisitor;

import java.util.List;

public class Read extends Stmt {
    public final List<String> varNames;

    public Read(Position position, List<String> varNames) {
        super(position);
        this.varNames = varNames;
    }

    @Override
    public <T> T accept(IAstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
