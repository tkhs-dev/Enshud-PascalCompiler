package enshud.pascal.ast.nodes.expr;

import enshud.pascal.ast.IAstVisitor;
import enshud.pascal.ast.nodes.AstNode;
import enshud.pascal.type.Type;

import java.util.List;

public class ConstNode extends Expr {
    public final String value;
    public final Type type;

    public ConstNode(Position position, String value, Type type) {
        super(position);
        this.value = value;
        this.type = type;
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
