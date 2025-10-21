package enshud.s3.checker;

import enshud.pascal.ast.AstParseException;
import enshud.pascal.ast.AstParser;
import enshud.pascal.ast.IAstVisitor;
import enshud.pascal.ast.nodes.*;
import enshud.pascal.ast.nodes.expr.*;
import enshud.pascal.ast.nodes.stmt.*;
import enshud.pascal.type.Type;
import enshud.token.FileFormatException;
import enshud.token.Token;
import enshud.token.TokenFile;

import java.io.IOException;
import java.util.*;

public class Checker {

	/**
	 * サンプルmainメソッド．
	 * 単体テストの対象ではないので自由に改変しても良い．
	 */
	public static void main(final String[] args) {
		// normalの確認
		System.out.println(new Checker().run("data/ts/normal01.ts"));
		System.out.println(new Checker().run("data/ts/normal02.ts"));

		// synerrの確認
		System.out.println(new Checker().run("data/ts/synerr01.ts"));
		System.out.println(new Checker().run("data/ts/synerr02.ts"));

		// semerrの確認
		System.out.println(new Checker().run("data/ts/semerr01.ts"));
		System.out.println(new Checker().run("data/ts/semerr02.ts"));
	}

	/**
	 * TODO
	 * 
	 * 開発対象となるChecker実行メソッド．
	 * 以下の仕様を満たすこと．
	 * 
	 * 仕様:
	 * 第一引数で指定されたtsファイルを読み込み，意味解析を行う．
	 * 意味的に正しい場合は"OK"を，正しくない場合は"Semantic error: line"という文字列とともに，
	 * 最初のエラーを見つけた行の番号を返すこと （例: "Semantic error: line 6"）．
	 * また，構文的なエラーが含まれる場合もエラーメッセージを返すこと （例： "Syntax error: line 1"）．
	 * 入力ファイル内に複数のエラーが含まれる場合は，最初に見つけたエラーのみを返すこと．
	 * 入力ファイルが見つからない場合は"File not found"を返すこと．
	 * 
	 * @param inputFileName 入力tsファイル名
	 */
	public String run(final String inputFileName) {
        try {
            TokenFile tf = new TokenFile(inputFileName);
            List<Token> tokens = tf.getTokens();
            AstParser parser = new AstParser(tokens);
            AstNode ast = parser.parse();
            SemanticCheckVisitor visitor = new SemanticCheckVisitor();
            ast.accept(visitor);
            // 構文解析が成功した場合
            return "OK";
        } catch (FileFormatException | IOException e) {
            return "File not found";
        } catch (AstParseException e) {
            return "Syntax error: line " + e.token.line;
        } catch (SemanticException e) {
            return "Semantic error: line " + e.line;
        }
    }
}

class SemanticCheckVisitor implements IAstVisitor<Object> {
    private final Deque<Map<String, Type>> scopes = new ArrayDeque<>();
    private final Map<String, SubProgDeclInfo> procedures = new HashMap<>();

    private void enterScope() {
        scopes.push(new HashMap<>());
    }

    private void exitScope() {
        scopes.pop();
    }

    private void declareVariable(String name, Type type, int line) {
        Map<String, Type> currentScope = scopes.peek();
        if (currentScope.containsKey(name)) {
            throw new SemanticException("Variable already declared: " + name, line);
        }
        currentScope.put(name, type);
    }

    private void declareProcedure(String name, List<Type> aparams, int line) {
        if (procedures.containsKey(name)) {
            throw new SemanticException("Procedure already declared: " + name, line);
        }
        procedures.put(name, new SubProgDeclInfo(aparams));
    }

    private Type lookupVariable(String name, int line) {
        for (Map<String, Type> scope : scopes) {
            if (scope.containsKey(name)) {
                return scope.get(name);
            }
        }
        throw new SemanticException("Undeclared variable: " + name, line);
    }

    @Override
    public Void visit(ProgramNode node) {
        enterScope();
        node.block.accept(this);
        node.statement.accept(this);
        exitScope();
        return null;
    }

    @Override
    public Void visit(BlockNode node) {
        for (VarDeclNode varDecl : node.varDecls) {
            varDecl.accept(this);
        }
        for (SubProgDeclNode subProgDecl : node.subProgDecls) {
            subProgDecl.accept(this);
        }
        return null;
    }

    @Override
    public Void visit(VarDeclNode node) {
        Type type = (Type) node.varType.accept(this);
        for (String varName : node.varNames) {
            declareVariable(varName, type, node.getPosition().startLine);
        }
        return null;
    }

    @Override
    public Void visit(SubProgDeclNode node) {
        enterScope();
        node.header.accept(this);
        for (VarDeclNode varDecl : node.varDecls) {
            varDecl.accept(this);
        }
        node.statement.accept(this);
        exitScope();
        return null;
    }

    @Override
    public Void visit(SubProgHeaderNode node) {
        var paramTypes = new ArrayList<Type>();
        for (FormalParamNode param : node.params) {
            Type type = (Type) param.accept(this);
            for (int i = 0; i < param.params.size(); i++) {
                paramTypes.add(type);
            }
        }
        declareProcedure(node.name, paramTypes, node.getPosition().startLine);
        return null;
    }

    @Override
    public Type visit(FormalParamNode node) {
        Type type = (Type) node.type.accept(this);
        for (String paramName : node.params) {
            declareVariable(paramName, type, node.getPosition().startLine);
        }
        return type;
    }

    @Override
    public Type visit(ArrayTypeNode node) {
        Type elementType = (Type) node.elementType.accept(this);
        return new enshud.pascal.type.ArrayType(node.lowerBound, node.upperBound, elementType);
    }

    @Override
    public Type visit(StandardTypeNode node) {
        switch (node.name) {
            case "integer":
                return Type.INTEGER;
            case "char":
                return Type.CHAR;
            case "boolean":
                return Type.BOOLEAN;
            default:
                throw new SemanticException("Unknown type: " + node.name, node.getPosition().startLine);
        }
    }

    @Override
    public Void visit(CompoundStmtNode node) {
        enterScope();
        for (Stmt stmt : node.stmts) {
            stmt.accept(this);
        }
        exitScope();
        return null;
    }

    @Override
    public Void visit(AssignmentNode node) {
        Type expr = (Type) node.expr.accept(this);
        Type var = (Type) node.var.accept(this);
        if (!expr.equals(var)) {
            throw new SemanticException("Type mismatch in assignment: " + expr + " vs " + var, node.getPosition().startLine);
        }
        return null;
    }

    @Override
    public Void visit(CallNode node) {
        var argTypes = node.args.stream()
                .map(arg -> (Type) arg.accept(this))
                .toList();
        if (!procedures.containsKey(node.name)) {
            throw new SemanticException("Undeclared procedure: " + node.name, node.getPosition().startLine);
        }
        if (procedures.get(node.name).paramTypes.size() != node.args.size()) {
            throw new SemanticException("Argument count mismatch in procedure call: " + node.name, node.getPosition().startLine);
        }
        if (!procedures.get(node.name).paramTypes.isEmpty() && !procedures.get(node.name).paramTypes.stream().allMatch(
                type -> type.equals(argTypes.get(procedures.get(node.name).paramTypes.indexOf(type)))
        )) {
            throw new SemanticException("Argument type mismatch in procedure call: " + node.name, node.getPosition().startLine);
        }
        return null;
    }

    @Override
    public Void visit(WriteNode node) {
        for (Expr expr : node.exprs) {
            expr.accept(this);
        }
        return null;
    }

    @Override
    public Void visit(ReadNode node) {
        return null;
    }

    @Override
    public Void visit(IfNode node) {
        Type condition = (Type) node.condition.accept(this);
        node.thenBranch.accept(this);
        if (node.elseBranch != null) {
            node.elseBranch.accept(this);
        }
        if (!condition.equals(Type.BOOLEAN)) {
            throw new SemanticException("Condition in IF must be BOOLEAN", node.getPosition().startLine);
        }
        return null;
    }

    @Override
    public Void visit(WhileNode node) {
        Type condition = (Type) node.condition.accept(this);
        node.body.accept(this);
        if (!condition.equals(Type.BOOLEAN)) {
            throw new SemanticException("Condition in WHILE must be BOOLEAN", node.getPosition().startLine);
        }
        return null;
    }

    @Override
    public Type visit(BinaryOpNode node) {
        Type left = (Type) node.left.accept(this);
        Type right = (Type) node.right.accept(this);

        if (right == null) {
            return left;
        }

        if (!left.equals(right)) {
            // 型が異なる場合のエラー処理
            throw new SemanticException("Type mismatch", node.getPosition().startLine);
        }

        switch (node.op) {
            case ADD:
            case SUB:
            case MUL:
            case DIV:
            case MOD:
                if (!left.equals(Type.INTEGER) || !right.equals(Type.INTEGER)) {
                    throw new SemanticException("Type mismatch in arithmetic operation", node.getPosition().startLine);
                }
                return Type.INTEGER;
            case AND:
            case OR:
                if (!left.equals(Type.BOOLEAN) || !right.equals(Type.BOOLEAN)) {
                    throw new SemanticException("Type mismatch in logical operation", node.getPosition().startLine);
                }
                return Type.BOOLEAN;
            case EQ:
            case NEQ:
            case LT:
            case LTE:
            case GT:
            case GTE:
                return Type.BOOLEAN;
            default:
                throw new SemanticException("Unknown binary operator", node.getPosition().startLine);
        }
    }

    @Override
    public Type visit(ConstNode node) {
        return node.type;
    }

    @Override
    public Type visit(UnaryOpNode node) {
        return (Type) node.operand.accept(this);
    }

    @Override
    public Type visit(StandardVarNode node) {
        return lookupVariable(node.name, node.getPosition().startLine);
    }

    @Override
    public Type visit(IndexedVarNode node) {
        Type indexType = (Type) node.index.accept(this);
        if (!indexType.equals(Type.INTEGER)) {
            throw new SemanticException("Array index must be of type INTEGER", node.getPosition().startLine);
        }
        Type arrayType = lookupVariable(node.name, node.getPosition().startLine);
        if (!(arrayType instanceof enshud.pascal.type.ArrayType)) {
            throw new SemanticException("Variable is not an array: " + node.name, node.getPosition().startLine);
        }
        return ((enshud.pascal.type.ArrayType) arrayType).elementType;
    }

    class SubProgDeclInfo {
        public final List<Type> paramTypes;

        public SubProgDeclInfo(List<Type> paramTypes) {
            this.paramTypes = paramTypes;
        }
    }
}

class SemanticException extends RuntimeException {
    public final int line;

    public SemanticException(String message, int line) {
        super(message);
        this.line = line;
    }
}
