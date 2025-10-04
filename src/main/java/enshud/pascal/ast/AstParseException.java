package enshud.pascal.ast;

import enshud.token.Token;

public class AstParseException extends RuntimeException {
    public final Token token;

    public AstParseException(String message, Token token) {
        super(token != null ? message + " at line " + token.line : message);
        this.token = token;
    }
}
