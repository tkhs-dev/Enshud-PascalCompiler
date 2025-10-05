package enshud.pascal.ast.nodes;

import enshud.pascal.ast.IAstVisitor;

import java.util.List;

public class Block extends AstNode {
    public final List<VarDecl> varDecls;
    public final List<SubProgDecl> subProgDecls;

    public Block(Position position, List<VarDecl> varDecls, List<SubProgDecl> subProgDecls) {
        super(position);
        this.varDecls = varDecls;
        this.subProgDecls = subProgDecls;
    }

    @Override
    public <T> T accept(IAstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
