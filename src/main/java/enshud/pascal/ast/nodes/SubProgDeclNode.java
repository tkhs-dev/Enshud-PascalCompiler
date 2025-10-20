package enshud.pascal.ast.nodes;

import enshud.pascal.ast.IAstVisitor;
import enshud.pascal.ast.nodes.stmt.CompoundStmtNode;

import java.util.List;

public class SubProgDeclNode extends AstNode {
    public final SubProgHeaderNode header;
    public final List<VarDeclNode> varDecls;
    public final CompoundStmtNode statement;

    public SubProgDeclNode(Position position, SubProgHeaderNode header, List<VarDeclNode> varDecls, CompoundStmtNode statement) {
        super(position);
        this.header = header;
        this.varDecls = varDecls;
        this.statement = statement;
    }

    @Override
    public <T> T accept(IAstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public List<AstNode> getChildren() {
        List<AstNode> children = new java.util.ArrayList<>();
        children.add(header);
        children.addAll(varDecls);
        children.add(statement);
        return children;
    }
}
