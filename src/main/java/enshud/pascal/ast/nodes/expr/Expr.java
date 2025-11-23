package enshud.pascal.ast.nodes.expr;

import enshud.pascal.ast.nodes.AstNode;
import enshud.pascal.type.Type;

import java.util.List;
import java.util.Optional;

// Expression
public abstract class Expr extends AstNode {

    private Type type;

    public Expr(Position position) {
        super(position);
        this.type = null;
    }

    public Optional<Type> getType() {
        return Optional.ofNullable(type);
    }

    public void setType(Type type) {
        this.type = type;
    }


    @Override
    public List<AstNode> getChildren() {
        return List.of();
    }
}
