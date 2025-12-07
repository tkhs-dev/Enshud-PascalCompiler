package enshud.s3.checker;

import enshud.pascal.ast.IAstVisitor;
import enshud.pascal.ast.nodes.*;
import enshud.pascal.ast.nodes.expr.*;
import enshud.pascal.ast.nodes.stmt.*;
import enshud.pascal.type.Type;
import enshud.util.SerialManager;

import java.util.*;

public class SemanticCheckVisitor implements IAstVisitor<Object> {
    private final Deque<Scope> scopes = new ArrayDeque<>();
    private final Map<String, SubProgDeclInfo> procedures = new HashMap<>();

    private SerialManager scopeManager = new SerialManager("Scope");

    private void enterScope() {
        scopes.push(new Scope(scopeManager.New()));
    }

    private void exitScope() {
        scopes.pop();
    }

    private VariableInfo declareVariable(String name, Type type, int line) {
        Scope currentScope = scopes.peek();
        if (currentScope.variables.containsKey(name)) {
            throw new SemanticException("Variable already declared: " + name, line);
        }
        currentScope.put(name, type);
        return new VariableInfo(type, currentScope.id);
    }

    private void declareProcedure(String name, List<Type> aparams, int line) {
        if (procedures.containsKey(name)) {
            throw new SemanticException("Procedure already declared: " + name, line);
        }
        procedures.put(name, new SubProgDeclInfo(aparams));
    }

    private VariableInfo lookupVariable(String name, int line) {
        for (Scope scope : scopes) {
            if (scope.variables.containsKey(name)) {
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
        node.setType(type);
        List<String> newNames = new ArrayList<>();
        for (String varName : node.varNames) {
            var vinfo = declareVariable(varName, type, node.getPosition().startLine);
            newNames.add(varName+"@"+ vinfo.scopeId());
        }
        node.varNames = newNames;
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
        Type type = (Type) node.typeNode.accept(this);
        List<String> newNames = new ArrayList<>();
        for (String paramName : node.params) {
            var vinfo = declareVariable(paramName, type, node.getPosition().startLine);
            newNames.add(paramName+"@"+ vinfo.scopeId());
        }
        node.params = newNames;
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
            node.setType(left);
            return left;
        }

        if (!left.equals(right)) {
            // 型が異なる場合のエラー処理
            throw new SemanticException("Type mismatch", node.getPosition().startLine);
        }
        Type resultType;
        switch (node.op) {
            case ADD:
            case SUB:
            case MUL:
            case DIV:
            case MOD:
                if (!left.equals(Type.INTEGER) || !right.equals(Type.INTEGER)) {
                    throw new SemanticException("Type mismatch in arithmetic operation", node.getPosition().startLine);
                }
                resultType = Type.INTEGER;
                break;
            case AND:
            case OR:
                if (!left.equals(Type.BOOLEAN) || !right.equals(Type.BOOLEAN)) {
                    throw new SemanticException("Type mismatch in logical operation", node.getPosition().startLine);
                }
                resultType = Type.BOOLEAN;
                break;
            case EQ:
            case NEQ:
            case LT:
            case LTE:
            case GT:
            case GTE:
                resultType = Type.BOOLEAN;
                break;
            default:
                throw new SemanticException("Unknown binary operator", node.getPosition().startLine);
        }
        node.setType(resultType);
        return resultType;
    }

    @Override
    public Type visit(ConstNode node) {
        return node.getType().get();
    }

    @Override
    public Type visit(UnaryOpNode node) {
        var type =  (Type) node.operand.accept(this);
        node.setType(type);
        return type;
    }

    @Override
    public Type visit(StandardVarNode node) {
        var vinfo = lookupVariable(node.name, node.getPosition().startLine);
        node.name = node.name + "@" + vinfo.scopeId();
        node.setType(vinfo.type());
        return vinfo.type();
    }

    @Override
    public Type visit(IndexedVarNode node) {
        Type indexType = (Type) node.index.accept(this);
        if (!indexType.equals(Type.INTEGER)) {
            throw new SemanticException("Array index must be of type INTEGER", node.getPosition().startLine);
        }
        VariableInfo vinfo = lookupVariable(node.name, node.getPosition().startLine);
        if (vinfo.type() instanceof enshud.pascal.type.ArrayType arrayType){
            node.name = node.name + "@" + vinfo.scopeId();
            node.setType(arrayType.elementType);
            return arrayType.elementType;
        }
        throw new SemanticException("Variable is not an array: " + node.name, node.getPosition().startLine);
    }

    record SubProgDeclInfo(List<Type> paramTypes) {
    }

    static class Scope {
        public final Map<String, Type> variables;
        public final String id;
        public Scope(String id) {
            this.variables = new HashMap<>();
            this.id = id;
        }

        public void put(String name, Type type) {
            variables.put(name, type);
        }

        public VariableInfo get(String name) {
            return new VariableInfo(variables.get(name), id);
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

record VariableInfo(Type type, String scopeId) {
}