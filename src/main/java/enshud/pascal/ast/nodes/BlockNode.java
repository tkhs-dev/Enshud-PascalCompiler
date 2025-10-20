package enshud.pascal.ast.nodes;

import enshud.pascal.ast.IAstVisitor;

import java.util.List;

public class BlockNode extends AstNode {
    public final List<VarDeclNode> varDecls;
    public final List<SubProgDeclNode> subProgDecls;

    public BlockNode(Position position, List<VarDeclNode> varDecls, List<SubProgDeclNode> subProgDecls) {
        super(position);
        this.varDecls = varDecls;
        this.subProgDecls = subProgDecls;
    }

    @Override
    public <T> T accept(IAstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public List<AstNode> getChildren() {
        List <AstNode> children = new java.util.ArrayList<>();
        children.addAll(varDecls);
        children.addAll(subProgDecls);
        return children;
    }
}
