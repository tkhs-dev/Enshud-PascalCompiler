package enshud.token;

public class Token {
    public final String token;
    public final String tokenName;
    public final int id;
    public final int line;

    public Token(String token, String tokenName, int id, int line) {
        this.token = token;
        this.tokenName = tokenName;
        this.id = id;
        this.line = line;
    }

    @Override
    public String toString() {
        return String.format("token: %s, tokenName: %s, id: %d, line: %d", token, tokenName, id, line);
    }
}
