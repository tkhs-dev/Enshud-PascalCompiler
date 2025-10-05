package enshud.pascal.ast;

import enshud.pascal.TokenDef;
import enshud.pascal.ast.nodes.*;
import enshud.pascal.ast.nodes.expr.*;
import enshud.pascal.ast.nodes.stmt.*;
import enshud.pascal.type.Type;
import enshud.token.Token;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AstParser {
    private final List<Token> tokens;
    private int pos = 0;

    public AstParser(List<Token> tokens) {
        this.tokens = tokens;
    }

    private Token peek() {
        if (pos < tokens.size()) {
            return tokens.get(pos);
        }
        return null;
    }

    private Token consume() {
        if (pos < tokens.size()) {
            return tokens.get(pos++);
        }
        return null;
    }

    private Token expect(TokenDef expect) throws AstParseException {
        Token token = consume();
        if (token == null || token.id != expect.id) {
            throw new AstParseException("Expected " + expect.tokenName + " but get " + token.token + " instead", token);
        }
        return token;
    }

    private boolean check(TokenDef check) {
        Token token = peek();
        return token != null && token.id == check.id;
    }

    public AstNode parse() throws AstParseException {
        return parseProgram();
    }

    private Program parseProgram() {
        Token start = expect(TokenDef.PROGRAM);
        String programName = expect(TokenDef.IDENT).token;
        expect(TokenDef.SEMICOLON);
        Block block = parseBlock();
        CompoundStmt stmt = parseCompoundStmt();
        Token end = expect(TokenDef.DOT);
        AstNode.Position position = AstNode.Position.fromToken(start, end);
        return new Program(position, programName, block, stmt);
    }

    private Block parseBlock() {
        Token start = peek();
        List<VarDecl> varDecls = parseVarDecls();
        List<SubProgDecl> subProgDecls = parseSubProgDecls();
        Token end = peek();
        AstNode.Position position = AstNode.Position.fromToken(start, end);
        return new Block(position, varDecls, subProgDecls);
    }

    private List<VarDecl> parseVarDecls() {
        List<VarDecl> varDecls = new java.util.ArrayList<>();
        if (check(TokenDef.VAR)) {
            consume(); // 'var'
            do {
                Token start = peek();
                List<String> varNames = new java.util.ArrayList<>();
                varNames.add(expect(TokenDef.IDENT).token);
                while (check(TokenDef.COMMA)) {
                    consume(); // ','
                    varNames.add(expect(TokenDef.IDENT).token);
                }
                expect(TokenDef.COLON); // ':'
                TypeNode varType = parseType();
                expect(TokenDef.SEMICOLON); // ';'
                Token end = peek();
                AstNode.Position position = AstNode.Position.fromToken(start, end);
                varDecls.add(new VarDecl(position, varNames, varType));
            } while (check(TokenDef.IDENT));
        }
        return varDecls;
    }

    private TypeNode parseType() {
        if (check(TokenDef.ARRAY)) {
            return parseArrayType();
        } else {
            return parseStandardType();
        }
    }

    private StandardType parseStandardType() {
        if (check(TokenDef.INTEGER) || check(TokenDef.CHAR) || check(TokenDef.BOOLEAN)) {
            Token start = peek();
            AstNode.Position position = AstNode.Position.fromToken(start, start);
            return new StandardType(position, consume().token);
        } else {
            throw new AstParseException("Expected standard type", peek());
        }
    }

    private ArrayType parseArrayType() {
        Token start = peek();
        consume(); // 'array'
        expect(TokenDef.LBRACKET); // '['
        int lower = 0;
        int upper = 0;
        try {
            lower = Integer.parseInt(expect(TokenDef.CONSTANT).token);
            expect(TokenDef.RANGE); // '..'
            upper = Integer.parseInt(expect(TokenDef.CONSTANT).token);
        } catch (NumberFormatException e) {
            throw new AstParseException("Array size must be a number", peek());
        }
        expect(TokenDef.RBRACKET); // ']'
        expect(TokenDef.OF); // 'of'
        StandardType elementType = parseStandardType();
        Token end = peek();
        AstNode.Position position = AstNode.Position.fromToken(start, end);
        return new ArrayType(position, lower, upper, elementType);
    }

    private List<SubProgDecl> parseSubProgDecls() {
        List<SubProgDecl> subProgDecls = new java.util.ArrayList<>();
        while (check(TokenDef.PROCEDURE)) {
            subProgDecls.add(parseSubProgDecl());
            expect(TokenDef.SEMICOLON);
        }
        return subProgDecls;
    }

    private SubProgDecl parseSubProgDecl() {
        Token start = peek();
        SubProgHeader header = parseSubProgHeader();
        List<VarDecl> varDecls = parseVarDecls();
        CompoundStmt statement = parseCompoundStmt();
        Token end = peek();
        AstNode.Position position = AstNode.Position.fromToken(start, end);
        return new SubProgDecl(position, header, varDecls, statement);
    }

    private SubProgHeader parseSubProgHeader() {
        if (check(TokenDef.PROCEDURE)) {
            Token start = peek();
            consume(); // 'procedure'
            String name = expect(TokenDef.IDENT).token;
            List<FormalParam> params = parseFormalParams();
            expect(TokenDef.SEMICOLON); // ';'
            Token end = peek();
            AstNode.Position position = AstNode.Position.fromToken(start, end);
            return new SubProgHeader(position, name, params);
        } else {
            throw new AstParseException("Expected procedure", peek());
        }
    }

    private List<FormalParam> parseFormalParams() {
        List<FormalParam> params = new java.util.ArrayList<>();
        if (check(TokenDef.LPAREN)) {
            consume(); // '('
            do {
                Token start = peek();
                List<String> paramNames = new java.util.ArrayList<>();
                paramNames.add(expect(TokenDef.IDENT).token);
                while (check(TokenDef.COMMA)) {
                    consume(); // ','
                    paramNames.add(expect(TokenDef.IDENT).token);
                }
                expect(TokenDef.COLON); // ':'
                TypeNode type = parseType();
                Token end = peek();
                AstNode.Position position = AstNode.Position.fromToken(start, end);
                params.add(new FormalParam(position, paramNames, type));
                if (!check(TokenDef.SEMICOLON)) {
                    break;
                }
                consume(); // ';'
            } while (true);
            expect(TokenDef.RPAREN); // ')'
        }
        return params;
    }

    private CompoundStmt parseCompoundStmt() {
        Token start = peek();
        expect(TokenDef.BEGIN);
        List<Stmt> statements = new java.util.ArrayList<>();
        do {
            if (check(TokenDef.END)) {
                break;
            }
            statements.add(parseStatement());
            if (check(TokenDef.SEMICOLON)) {
                consume(); // ';'
            } else {
                break;
            }
        } while (true);
        expect(TokenDef.END);
        Token end = peek();
        AstNode.Position position = AstNode.Position.fromToken(start, end);
        return new CompoundStmt(position, statements);
    }

    private Stmt parseStatement() {
        if (check(TokenDef.IDENT)) {
            Token nextToken = tokens.get(pos + 1);
            if (nextToken.id == TokenDef.ASSIGN.id || nextToken.id == TokenDef.LBRACKET.id) {
                return parseAssignment();
            } else if (nextToken.id == TokenDef.LPAREN.id || nextToken.id == TokenDef.SEMICOLON.id) {
                return parseCall();
            } else {
                throw new AstParseException("Invalid statement", nextToken);
            }
        } else if (check(TokenDef.BEGIN)) {
            return parseCompoundStmt();
        } else if (check(TokenDef.WRITELN)) {
            return parseWrite();
        } else if (check(TokenDef.READLN)) {
            return parseRead();
        } else if (check(TokenDef.IF)) {
            return parseIf();
        } else if (check(TokenDef.WHILE)) {
            return parseWhile();
        } else {
            throw new AstParseException("Invalid statement", peek());
        }
    }

    private Assignment parseAssignment() {
        Token start = peek();
        VarNode var = parseVar();
        expect(TokenDef.ASSIGN); // ':='
        Expr expr = parseExpr();
        Token end = peek();
        AstNode.Position position = AstNode.Position.fromToken(start, end);
        return new Assignment(position, var, expr);
    }

    private Call parseCall() {
        Token start = peek();
        String name = expect(TokenDef.IDENT).token;
        List<Expr> args = new java.util.ArrayList<>();
        if (check(TokenDef.LPAREN)) {
            consume(); // '('
            if (!check(TokenDef.RPAREN)) {
                args.add(parseExpr());
                while (check(TokenDef.COMMA)) {
                    consume(); // ','
                    args.add(parseExpr());
                }
            }
            expect(TokenDef.RPAREN); // ')'
        }
        Token end = peek();
        AstNode.Position position = AstNode.Position.fromToken(start, end);
        return new Call(position, name, args);
    }

    private Write parseWrite() {
        Token start = peek();
        expect(TokenDef.WRITELN);
        List<Expr> exprs = new java.util.ArrayList<>();
        if (check(TokenDef.LPAREN)) {
            consume(); // '('
            if (!check(TokenDef.RPAREN)) {
                exprs.add(parseExpr());
                while (check(TokenDef.COMMA)) {
                    consume(); // ','
                    exprs.add(parseExpr());
                }
            }
            expect(TokenDef.RPAREN); // ')'
        }
        Token end = peek();
        AstNode.Position position = AstNode.Position.fromToken(start, end);
        return new Write(position, exprs);
    }

    private Read parseRead() {
        Token start = peek();
        expect(TokenDef.READLN);
        List<String> varNames = new java.util.ArrayList<>();
        expect(TokenDef.LPAREN); // '('
        varNames.add(expect(TokenDef.IDENT).token);
        while (check(TokenDef.COMMA)) {
            consume(); // ','
            varNames.add(expect(TokenDef.IDENT).token);
        }
        expect(TokenDef.RPAREN); // ')'
        Token end = peek();
        AstNode.Position position = AstNode.Position.fromToken(start, end);
        return new Read(position, varNames);
    }

    private If parseIf() {
        Token start = peek();
        expect(TokenDef.IF);
        Expr condition = parseExpr();
        expect(TokenDef.THEN);
        CompoundStmt thenStmt = parseCompoundStmt();
        CompoundStmt elseStmt = null;
        if (check(TokenDef.ELSE)) {
            consume(); // 'else'
            elseStmt = parseCompoundStmt();
        }
        Token end = peek();
        AstNode.Position position = AstNode.Position.fromToken(start, end);
        return new If(position, condition, thenStmt, elseStmt);
    }

    private While parseWhile() {
        Token start = peek();
        expect(TokenDef.WHILE);
        Expr condition = parseExpr();
        expect(TokenDef.DO);
        CompoundStmt body = parseCompoundStmt();
        Token end = peek();
        AstNode.Position position = AstNode.Position.fromToken(start, end);
        return new While(position, condition, body);
    }

    private Expr parseExpr() {
        return parseBinaryOp(1);
    }

    private Expr parseBinaryOp(int minPriority) {
        Token start = peek();
        Expr left = parseUnaryOp();
        while (true) {
            OperatorInfo opInfo = BINARY_OPERATORS.get(TokenDef.getTokenDef(peek().id));

            if (opInfo == null || opInfo.priority < minPriority) {
                break;
            }
            consume(); // operator

            int nextMinPriority = opInfo.rightAssociative ? opInfo.priority : opInfo.priority + 1;
            Expr right = parseBinaryOp(nextMinPriority);
            BinaryOp.Op op = opInfo.op;
            Token end = peek();
            AstNode.Position position = AstNode.Position.fromToken(start, end);
            left = new BinaryOp(position, op, left, right);
        }
        return left;
    }

    private Expr parseUnaryOp() {
        if (check(TokenDef.PLUS) || check(TokenDef.MINUS) || check(TokenDef.NOT)) {
            Token token = consume();
            Expr expr = parsePrimary();
            UnaryOp.Op op;
            if (token.id == TokenDef.PLUS.id) {
                op = UnaryOp.Op.PLUS;
            } else if (token.id == TokenDef.MINUS.id) {
                op = UnaryOp.Op.MINUS;
            } else if (token.id == TokenDef.NOT.id) {
                op = UnaryOp.Op.NOT;
            } else {
                throw new AstParseException("Invalid unary operator", token);
            }
            AstNode.Position position = AstNode.Position.fromToken(token, peek());
            return new UnaryOp(position, op, expr);
        } else {
            return parsePrimary();
        }
    }

    private Expr parsePrimary() {
        if (check(TokenDef.LPAREN)) {
            consume(); // '('
            Expr expr = parseExpr();
            expect(TokenDef.RPAREN); // ')'
            return expr;
        } else if (check(TokenDef.IDENT)) {
            return parseVar();
        } else if (check(TokenDef.CONSTANT)) {
            Token start = peek();
            String constToken = expect(TokenDef.CONSTANT).token;
            try {
                Integer.parseInt(constToken);
                AstNode.Position position = AstNode.Position.fromToken(start, start);
                return new Const(position, constToken, Type.INTEGER);
            } catch (NumberFormatException e) {
                throw new AstParseException("Invalid constant", peek());
            }
        } else if (check(TokenDef.TRUE)) {
            Token start = consume(); // 'true'
            AstNode.Position position = AstNode.Position.fromToken(start, start);
            return new Const(position, "true", Type.BOOLEAN);
        } else if (check(TokenDef.FALSE)) {
            Token start = consume(); // 'false'
            AstNode.Position position = AstNode.Position.fromToken(start, start);
            return new Const(position, "false", Type.BOOLEAN);
        } else if (check(TokenDef.STRING)) {
            Token start = peek();
            String strValue = expect(TokenDef.STRING).token;
            // Remove the prefix quote and suffix quote/br
            strValue = strValue.substring(1, strValue.length() - 1);
            AstNode.Position position = AstNode.Position.fromToken(start, start);
            if (strValue.length() == 1) {
                return new Const(position, strValue, Type.CHAR);
            }
            return new Const(position, strValue, new enshud.pascal.type.ArrayType(1, strValue.length(), Type.CHAR));
        } else {
            throw new AstParseException("Invalid primary expression", peek());
        }
    }

    private VarNode parseVar() {
        Token start = peek();
        String varName = expect(TokenDef.IDENT).token;
        if (check(TokenDef.LBRACKET)) {
            consume(); // '['
            Expr index = parseExpr();
            expect(TokenDef.RBRACKET); // ']'
            Token end = peek();
            AstNode.Position position = AstNode.Position.fromToken(start, end);
            return new IndexedVar(position, varName, index);
        } else {
            AstNode.Position position = AstNode.Position.fromToken(start, start);
            return new StandardVar(position, varName);
        }
    }

    private static class OperatorInfo {
        final BinaryOp.Op op;
        final int priority;
        final boolean rightAssociative;

        OperatorInfo(BinaryOp.Op op, int priority, boolean rightAssociative) {
            this.op = op;
            this.priority = priority;
            this.rightAssociative = rightAssociative;
        }
    }

    private static final Map<TokenDef, OperatorInfo> BINARY_OPERATORS = new HashMap<>();

    static {
        // priority: 大きいほど強い結合
        BINARY_OPERATORS.put(TokenDef.OR, new OperatorInfo(BinaryOp.Op.OR, 1, false));
        BINARY_OPERATORS.put(TokenDef.AND, new OperatorInfo(BinaryOp.Op.AND, 2, false));

        BINARY_OPERATORS.put(TokenDef.EQ, new OperatorInfo(BinaryOp.Op.EQ, 3, false));
        BINARY_OPERATORS.put(TokenDef.NEQ, new OperatorInfo(BinaryOp.Op.NEQ, 3, false));
        BINARY_OPERATORS.put(TokenDef.LT, new OperatorInfo(BinaryOp.Op.LT, 3, false));
        BINARY_OPERATORS.put(TokenDef.LTE, new OperatorInfo(BinaryOp.Op.LTE, 3, false));
        BINARY_OPERATORS.put(TokenDef.GT, new OperatorInfo(BinaryOp.Op.GT, 3, false));
        BINARY_OPERATORS.put(TokenDef.GTE, new OperatorInfo(BinaryOp.Op.GTE, 3, false));

        BINARY_OPERATORS.put(TokenDef.PLUS, new OperatorInfo(BinaryOp.Op.ADD, 4, false));
        BINARY_OPERATORS.put(TokenDef.MINUS, new OperatorInfo(BinaryOp.Op.SUB, 4, false));

        BINARY_OPERATORS.put(TokenDef.STAR, new OperatorInfo(BinaryOp.Op.MUL, 5, false));
        BINARY_OPERATORS.put(TokenDef.DIV, new OperatorInfo(BinaryOp.Op.DIV, 5, false));
        BINARY_OPERATORS.put(TokenDef.MOD, new OperatorInfo(BinaryOp.Op.MOD, 5, false));
    }

}
