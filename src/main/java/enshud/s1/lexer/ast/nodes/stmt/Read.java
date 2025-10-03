package enshud.s1.lexer.ast.nodes.stmt;

import enshud.s1.lexer.ast.AstVisitor;

import java.util.List;

public class Read extends Stmt {
    public final List<String> varNames;

    public Read(List<String> varNames) {
        this.varNames = varNames;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
