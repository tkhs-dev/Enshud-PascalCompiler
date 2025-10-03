package enshud.s1.lexer.ast.nodes;

import enshud.s1.lexer.ast.AstVisitor;

import java.util.List;

public class Block extends AstNode {
    public final List<VarDecl> varDecls;
    public final List<SubProgDecl> subProgDecls;

    public Block(List<VarDecl> varDecls, List<SubProgDecl> subProgDecls) {
        this.varDecls = varDecls;
        this.subProgDecls = subProgDecls;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
