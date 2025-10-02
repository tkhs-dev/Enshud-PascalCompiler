package enshud.pascal;

public class Token {
    public static final Token AND = new Token(0, "SAND", 1);

    public final int id; // トークンID
    public final String tokenName; // トークン名
    public final int priority; // 優先度（数値が小さいほど優先度が高い）

    // コンストラクタ
    private Token(int Id, String tokenName, int priority) {
        this.id = Id;
        this.tokenName = tokenName;
        this.priority = priority;
    }
}
