package enshud.pascal.ast.nodes;

import enshud.pascal.ast.AstVisitor;

import java.util.List;

public class SubProgDecl extends AstNode {
    public final SubProgHeader header;
    public final List<VarDecl> varDecls;
    public final CompoundStmt statement;

    public SubProgDecl(Position position, SubProgHeader header, List<VarDecl> varDecls, CompoundStmt statement) {
        super(position);
        this.header = header;
        this.varDecls = varDecls;
        this.statement = statement;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
