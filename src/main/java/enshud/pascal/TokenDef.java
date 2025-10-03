package enshud.pascal;

import java.util.List;

public class TokenDef {
    public static final TokenDef COMMENT = new TokenDef(-1, "SCOMMENT", 1, "\\{[^}]*\\}");
    public static final TokenDef AND = new TokenDef(0, "SAND", 2, "and");
    public static final TokenDef ARRAY = new TokenDef(1, "SARRAY", 2, "array");
    public static final TokenDef BEGIN = new TokenDef(2, "SBEGIN", 2, "begin");
    public static final TokenDef BOOLEAN = new TokenDef(3, "SBOOLEAN", 2, "boolean");
    public static final TokenDef CHAR = new TokenDef(4, "SCHAR", 2, "char");
    public static final TokenDef DIV = new TokenDef(5, "SDIVD", 2, "div|/");
    public static final TokenDef DO = new TokenDef(6, "SDO", 2, "do");
    public static final TokenDef ELSE = new TokenDef(7, "SELSE", 2, "else");
    public static final TokenDef END = new TokenDef(8, "SEND", 2, "end");
    public static final TokenDef FALSE = new TokenDef(9, "SFALSE", 2, "false");
    public static final TokenDef IF = new TokenDef(10, "SIF", 2, "if");
    public static final TokenDef INTEGER = new TokenDef(11, "SINTEGER", 2, "integer");
    public static final TokenDef MOD = new TokenDef(12, "SMOD", 2, "mod");
    public static final TokenDef NOT = new TokenDef(13, "SNOT", 2, "not");
    public static final TokenDef OF = new TokenDef(14, "SOF", 2, "of");
    public static final TokenDef OR = new TokenDef(15, "SOR", 2, "or");
    public static final TokenDef PROCEDURE = new TokenDef(16, "SPROCEDURE", 4, "procedure");
    public static final TokenDef PROGRAM = new TokenDef(17, "SPROGRAM", 4, "program");
    public static final TokenDef READLN = new TokenDef(18, "SREADLN", 4, "readln");
    public static final TokenDef THEN = new TokenDef(19, "STHEN", 4, "then");
    public static final TokenDef TRUE = new TokenDef(20, "STRUE", 4, "true");
    public static final TokenDef VAR = new TokenDef(21, "SVAR", 4, "var");
    public static final TokenDef WHILE = new TokenDef(22, "SWHILE", 4, "while");
    public static final TokenDef WRITELN = new TokenDef(23, "SWRITELN", 4, "writeln");
    public static final TokenDef EQ = new TokenDef(24, "SEQUAL", 3, "=");
    public static final TokenDef NEQ = new TokenDef(25, "SNOTEQUAL", 2, "<>");
    public static final TokenDef LT = new TokenDef(26, "SLESS", 3, "<");
    public static final TokenDef LTE = new TokenDef(27, "SLESSEQUAL", 2, "<=");
    public static final TokenDef GTE = new TokenDef(28, "SGREATEQUAL", 2, ">=");
    public static final TokenDef GT = new TokenDef(29, "SGREAT", 3, ">");
    public static final TokenDef PLUS = new TokenDef(30, "SPLUS", 2, "\\+");
    public static final TokenDef MINUS = new TokenDef(31, "SMINUS", 2, "-");
    public static final TokenDef STAR = new TokenDef(32, "SSTAR", 2, "\\*");
    public static final TokenDef LPAREN = new TokenDef(33, "SLPAREN", 2, "\\(");
    public static final TokenDef RPAREN = new TokenDef(34, "SRPAREN", 2, "\\)");
    public static final TokenDef LBRACKET = new TokenDef(35, "SLBRACKET", 2, "\\[");
    public static final TokenDef RBRACKET = new TokenDef(36, "SRBRACKET", 2, "\\]");
    public static final TokenDef SEMICOLON = new TokenDef(37, "SSEMICOLON", 3, ";");
    public static final TokenDef COLON = new TokenDef(38, "SCOLON", 2, ":");
    public static final TokenDef RANGE = new TokenDef(39, "SRANGE", 2, "\\.\\.");
    public static final TokenDef ASSIGN = new TokenDef(40, "SASSIGN", 2, ":=");
    public static final TokenDef COMMA = new TokenDef(41, "SCOMMA", 2, ",");
    public static final TokenDef DOT = new TokenDef(42, "SDOT", 3, "\\.");
    public static final TokenDef IDENT = new TokenDef(43, "SIDENTIFIER", 5, "[a-zA-Z_][a-zA-Z0-9_]*");
    public static final TokenDef CONSTANT = new TokenDef(44, "SCONSTANT", 2, "[0-9]+");
    public static final TokenDef STRING = new TokenDef(45, "SSTRING", 1, "'([^']|'')*'");

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
    private TokenDef(int Id, String tokenName, int priority, String regex) {
        this.id = Id;
        this.tokenName = tokenName;
        this.priority = priority;
        this.regex = regex;
    }
}
