package enshud.pascal.ast.nodes;

import enshud.pascal.ast.IAstVisitor;
import enshud.token.Token;

import java.util.List;

// 抽象構文木ノード
public abstract class AstNode {
    private final Position position;

    protected AstNode(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public abstract <T> T accept(IAstVisitor<T> visitor);

    public abstract List<AstNode> getChildren();

    public static class Position {
        public final int startLine;
        public final int endLine;

        public Position(int startLine, int endLine) {
            this.startLine = startLine;
            this.endLine = endLine;
        }

        public static Position fromToken(Token start, Token end) {
            return new Position(start.line, end.line);
        }
    }
}

