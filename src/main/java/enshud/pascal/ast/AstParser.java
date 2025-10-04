package enshud.pascal.ast;

import enshud.pascal.TokenDef;
import enshud.pascal.ast.nodes.*;
import enshud.pascal.ast.nodes.expr.*;
import enshud.pascal.ast.nodes.stmt.*;
import enshud.token.Token;

import java.util.List;

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

    private String expect(TokenDef expect) throws AstParseException {
        Token token = consume();
        if (token == null || token.id != expect.id) {
            throw new AstParseException("Expected " + expect.tokenName + " but get " + token.token + " instead", token);
        }
        return token.token;
    }

    private boolean check(TokenDef check) {
        Token token = peek();
        return token != null && token.id == check.id;
    }

    public AstNode parse() throws AstParseException {
        return parseProgram();
    }

    private Program parseProgram() {
        expect(TokenDef.PROGRAM);
        String programName = expect(TokenDef.IDENT);
        expect(TokenDef.SEMICOLON);
        Block block = parseBlock();
        CompoundStmt stmt = parseCompoundStmt();
        expect(TokenDef.DOT);
        return new Program(programName, block, stmt);
    }

    private Block parseBlock() {
        List<VarDecl> varDecls = parseVarDecls();
        List<SubProgDecl> subProgDecls = parseSubProgDecls();
        return new Block(varDecls, subProgDecls);
    }

    private List<VarDecl> parseVarDecls() {
        List<VarDecl> varDecls = new java.util.ArrayList<>();
        if (check(TokenDef.VAR)) {
            consume(); // 'var'
            do {
                List<String> varNames = new java.util.ArrayList<>();
                varNames.add(expect(TokenDef.IDENT));
                while (check(TokenDef.COMMA)) {
                    consume(); // ','
                    varNames.add(expect(TokenDef.IDENT));
                }
                expect(TokenDef.COLON); // ':'
                TypeNode varType = parseType();
                expect(TokenDef.SEMICOLON); // ';'
                varDecls.add(new VarDecl(varNames, varType));
            } while (check(TokenDef.IDENT));
        }
        return null;
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
            return new StandardType(consume().token);
        } else {
            throw new AstParseException("Expected standard type", peek());
        }
    }

    private ArrayType parseArrayType() {
        consume(); // 'array'
        expect(TokenDef.LBRACKET); // '['
        int lower = 0;
        int upper = 0;
        try {
            lower = Integer.parseInt(expect(TokenDef.CONSTANT));
            expect(TokenDef.RANGE); // '..'
            upper = Integer.parseInt(expect(TokenDef.CONSTANT));
        } catch (NumberFormatException e) {
            throw new AstParseException("Array size must be a number", peek());
        }
        expect(TokenDef.RBRACKET); // ']'
        expect(TokenDef.OF); // 'of'
        StandardType elementType = parseStandardType();
        return new ArrayType(lower, upper, elementType);
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
        SubProgHeader header = parseSubProgHeader();
        List<VarDecl> varDecls = parseVarDecls();
        CompoundStmt statement = parseCompoundStmt();
        return new SubProgDecl(header, varDecls, statement);
    }

    private SubProgHeader parseSubProgHeader() {
        if (check(TokenDef.PROCEDURE)) {
            consume(); // 'procedure'
            String name = expect(TokenDef.IDENT);
            List<FormalParam> params = parseFormalParams();
            expect(TokenDef.SEMICOLON); // ';'
            return new SubProgHeader(name, params);
        } else {
            throw new AstParseException("Expected procedure", peek());
        }
    }

    private List<FormalParam> parseFormalParams() {
        List<FormalParam> params = new java.util.ArrayList<>();
        if (check(TokenDef.LPAREN)) {
            consume(); // '('
            do {
                List<String> paramNames = new java.util.ArrayList<>();
                paramNames.add(expect(TokenDef.IDENT));
                while (check(TokenDef.COMMA)) {
                    consume(); // ','
                    paramNames.add(expect(TokenDef.IDENT));
                }
                expect(TokenDef.COLON); // ':'
                TypeNode type = parseType();
                params.add(new FormalParam(paramNames, type));
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
        return new CompoundStmt(statements);
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
        VarNode var = parseVar();
        expect(TokenDef.ASSIGN); // ':='
        Expr expr = parseExpr();
        return new Assignment(var, expr);
    }

    private Call parseCall() {
        String name = expect(TokenDef.IDENT);
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
        return new Call(name, args);
    }

    private Write parseWrite() {
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
        return new Write(exprs);
    }

    private Read parseRead() {
        expect(TokenDef.READLN);
        List<String> varNames = new java.util.ArrayList<>();
        expect(TokenDef.LPAREN); // '('
        varNames.add(expect(TokenDef.IDENT));
        while (check(TokenDef.COMMA)) {
            consume(); // ','
            varNames.add(expect(TokenDef.IDENT));
        }
        expect(TokenDef.RPAREN); // ')'
        return new Read(varNames);
    }

    private If parseIf() {
        expect(TokenDef.IF);
        Expr condition = parseExpr();
        expect(TokenDef.THEN);
        CompoundStmt thenStmt = parseCompoundStmt();
        CompoundStmt elseStmt = null;
        if (check(TokenDef.ELSE)) {
            consume(); // 'else'
            elseStmt = parseCompoundStmt();
        }
        return new If(condition, thenStmt, elseStmt);
    }

    private While parseWhile() {
        expect(TokenDef.WHILE);
        Expr condition = parseExpr();
        expect(TokenDef.DO);
        CompoundStmt body = parseCompoundStmt();
        return new While(condition, body);
    }

    private Expr parseExpr() {
        return parseBinaryOp();
    }

    private Expr parseBinaryOp() {
        Expr left = parseUnaryOp();
        while (check(TokenDef.PLUS) || check(TokenDef.MINUS) || check(TokenDef.STAR) || check(TokenDef.DIV) || check(TokenDef.MOD) ||
                check(TokenDef.EQ) || check(TokenDef.NEQ) || check(TokenDef.LT) || check(TokenDef.LTE) ||
                check(TokenDef.GT) || check(TokenDef.GTE) || check(TokenDef.AND) || check(TokenDef.OR)) {
            Token token = consume();
            Expr right = parseUnaryOp();
            BinaryOp.Op op;
            if (token.id == TokenDef.PLUS.id) {
                op = BinaryOp.Op.ADD;
            } else if (token.id == TokenDef.MINUS.id) {
                op = BinaryOp.Op.SUB;
            } else if (token.id == TokenDef.STAR.id) {
                op = BinaryOp.Op.MUL;
            } else if (token.id == TokenDef.DIV.id) {
                op = BinaryOp.Op.DIV;
            } else if (token.id == TokenDef.MOD.id) {
                op = BinaryOp.Op.MOD;
            } else if (token.id == TokenDef.EQ.id) {
                op = BinaryOp.Op.EQ;
            } else if (token.id == TokenDef.NEQ.id) {
                op = BinaryOp.Op.NEQ;
            } else if (token.id == TokenDef.LT.id) {
                op = BinaryOp.Op.LT;
            } else if (token.id == TokenDef.LTE.id) {
                op = BinaryOp.Op.LTE;
            } else if (token.id == TokenDef.GT.id) {
                op = BinaryOp.Op.GT;
            } else if (token.id == TokenDef.GTE.id) {
                op = BinaryOp.Op.GTE;
            } else if (token.id == TokenDef.AND.id) {
                op = BinaryOp.Op.AND;
            } else if (token.id == TokenDef.OR.id) {
                op = BinaryOp.Op.OR;
            } else {
                throw new AstParseException("Invalid binary operator", token);
            }
            left = new BinaryOp(op, left, right);
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
            return new UnaryOp(op, expr);
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
            String constToken = expect(TokenDef.CONSTANT);
            try {
                int intValue = Integer.parseInt(constToken);
                return new Const(intValue);
            } catch (NumberFormatException e) {
                throw new AstParseException("Invalid constant", peek());
            }
        } else if (check(TokenDef.TRUE)) {
            consume(); // 'true'
            return new Const(true);
        } else if (check(TokenDef.FALSE)) {
            consume(); // 'false'
            return new Const(false);
        } else if (check(TokenDef.STRING)) {
            String strValue = expect(TokenDef.STRING);
            // Remove the surrounding quotes
            strValue = strValue.substring(1, strValue.length() - 1);
            return new Const(strValue);
        } else {
            throw new AstParseException("Invalid primary expression", peek());
        }
    }

    private VarNode parseVar() {
        String varName = expect(TokenDef.IDENT);
        if (check(TokenDef.LBRACKET)) {
            consume(); // '['
            Expr index = parseExpr();
            expect(TokenDef.RBRACKET); // ']'
            return new IndexedVar(varName, index);
        } else {
            return new StandardVar(varName);
        }
    }


}
