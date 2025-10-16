package enshud.pascal;

import java.util.List;

public enum TokenDef {
    COMMENT(-1, "SCOMMENT", 1, "\\{[^}]*\\}"),
    AND(0, "SAND", 3, "and\\b"),
    ARRAY(1, "SARRAY", 3, "array\\b"),
    BEGIN(2, "SBEGIN", 3, "begin\\b"),
    BOOLEAN(3, "SBOOLEAN", 3, "boolean\\b"),
    CHAR(4, "SCHAR", 3, "char\\b"),
    DIV(5, "SDIVD", 3, "div\\b|/"),
    DO(6, "SDO", 3, "do\\b"),
    ELSE(7, "SELSE", 3, "else\\b"),
    END(8, "SEND", 3, "end\\b"),
    FALSE(9, "SFALSE", 3, "false\\b"),
    IF(10, "SIF", 3, "if\\b"),
    INTEGER(11, "SINTEGER", 3, "integer\\b"),
    MOD(12, "SMOD", 3, "mod\\b"),
    NOT(13, "SNOT", 3, "not\\b"),
    OF(14, "SOF", 3, "of\\b"),
    OR(15, "SOR", 3, "or\\b"),
    PROCEDURE(16, "SPROCEDURE", 3, "procedure\\b"),
    PROGRAM(17, "SPROGRAM", 3, "program\\b"),
    READLN(18, "SREADLN", 3, "readln\\b"),
    THEN(19, "STHEN", 3, "then\\b"),
    TRUE(20, "STRUE", 3, "true\\b"),
    VAR(21, "SVAR", 3, "var\\b"),
    WHILE(22, "SWHILE", 3, "while\\b"),
    WRITELN(23, "SWRITELN", 3, "writeln\\b"),
    EQ(24, "SEQUAL", 4, "="),
    NEQ(25, "SNOTEQUAL", 3, "<>"),
    LT(26, "SLESS", 4, "<"),
    LTE(27, "SLESSEQUAL", 3, "<="),
    GTE(28, "SGREATEQUAL", 3, ">="),
    GT(29, "SGREAT", 4, ">"),
    PLUS(30, "SPLUS", 3, "\\+"),
    MINUS(31, "SMINUS", 3, "-"),
    STAR(32, "SSTAR", 3, "\\*"),
    LPAREN(33, "SLPAREN", 3, "\\("),
    RPAREN(34, "SRPAREN", 3, "\\)"),
    LBRACKET(35, "SLBRACKET", 3, "\\["),
    RBRACKET(36, "SRBRACKET", 3, "\\]"),
    SEMICOLON(37, "SSEMICOLON", 3, ";"),
    COLON(38, "SCOLON", 4, ":"),
    RANGE(39, "SRANGE", 3, "\\.\\."),
    ASSIGN(40, "SASSIGN", 3, ":="),
    COMMA(41, "SCOMMA", 3, ","),
    DOT(42, "SDOT", 4, "\\."),
    IDENT(43, "SIDENTIFIER", 5, "[a-zA-Z][a-zA-Z0-9]*\\b"),
    CONSTANT(44, "SCONSTANT", 3, "[0-9]+"),
    STRING(45, "SSTRING", 2, "'([^']|'')*('|\\n)");

    public static final List<TokenDef> Tokens = List.of(
            COMMENT,
            AND, ARRAY, BEGIN, BOOLEAN, CHAR, DIV, DO, ELSE, END, FALSE,
            IF, INTEGER, MOD, NOT, OF, OR, PROCEDURE, PROGRAM, READLN, THEN,
            TRUE, VAR, WHILE, WRITELN,
            EQ, NEQ, LT, LTE, GTE, GT,
            PLUS, MINUS, STAR,
            LPAREN, RPAREN, LBRACKET, RBRACKET,
            SEMICOLON, COLON, RANGE, ASSIGN, COMMA, DOT,
            IDENT, CONSTANT, STRING
    );

    public static final List<TokenDef> SortedTokens = Tokens.stream()
            .sorted((t1, t2) -> {
                if (t1.priority != t2.priority) {
                    return Integer.compare(t1.priority, t2.priority); // 優先度で比較
                } else {
                    return Integer.compare(t2.regex.length(), t1.regex.length()); // 正規表現の長さで比較（長い方が優先）
                }
            })
            .toList();

    public final int id; // トークンID
    public final String tokenName; // トークン名
    public final int priority; // 優先度（数値が小さいほど優先度が高い）
    public final String regex; // 正規表現

    // コンストラクタ
    TokenDef(int Id, String tokenName, int priority, String regex) {
        this.id = Id;
        this.tokenName = tokenName;
        this.priority = priority;
        this.regex = regex;
    }

    public static TokenDef getTokenDef(int id) {
        for (TokenDef token : Tokens) {
            if (token.id == id) {
                return token;
            }
        }
        return null; // 見つからなかった場合はnullを返す
    }
}
