package enshud.pascal.ast.nodes;

import enshud.pascal.ast.IAstVisitor;
import enshud.pascal.ast.nodes.stmt.CompoundStmt;

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
    public <T> T accept(IAstVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public List<AstNode> getChildren() {
        return List.of();
    }
}
