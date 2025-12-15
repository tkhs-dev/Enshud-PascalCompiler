package enshud.s3.checker;

public class SemanticException extends RuntimeException {
    public final int line;

    public SemanticException(String message, int line) {
        super(message);
        this.line = line;
    }
}
