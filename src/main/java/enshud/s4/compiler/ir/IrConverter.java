package enshud.s4.compiler.ir;

import enshud.pascal.ast.IAstVisitor;
import enshud.pascal.ast.nodes.*;
import enshud.pascal.ast.nodes.expr.*;
import enshud.pascal.ast.nodes.stmt.*;
import enshud.pascal.type.ArrayType;
import enshud.pascal.type.Type;
import enshud.s4.compiler.ir.instructions.*;
import enshud.s4.compiler.ir.operand.Const;
import enshud.s4.compiler.ir.operand.Temp;
import enshud.s4.compiler.ir.operand.Variable;
import enshud.s4.compiler.ir.program.IrFunction;
import enshud.s4.compiler.ir.program.IrProgram;
import enshud.util.SerialManager;

import java.util.Optional;

import static enshud.pascal.type.Type.CHAR;
import static enshud.pascal.type.Type.INTEGER;

public class IrConverter {
    public static IrProgram convert(AstNode astProgram) {
        IrConverterVisitor visitor = new IrConverterVisitor();
        astProgram.accept(visitor);
        return visitor.getProgram();
    }
}

class IrConverterVisitor implements IAstVisitor<Optional<Temp>> {
    private final SerialManager tempManager;
    private final SerialManager labelManager;
    private IrFunction currentScope;
    private IrProgram program;

    public IrConverterVisitor() {
        this.tempManager = new SerialManager("t");
        this.labelManager = new SerialManager("L");
        this.program = new IrProgram();
    }

    private void visitAll(AstNode node) {
        node.getChildren().forEach(child -> child.accept(this));
    }

    public IrProgram getProgram() {
        return program;
    }

    @Override
    public Optional<Temp> visit(ProgramNode node) {
        currentScope = program.main;
        visitAll(node);
        return Optional.empty();
    }

    @Override
    public Optional<Temp> visit(BlockNode node) {
        visitAll(node);
        return Optional.empty();
    }

    @Override
    public Optional<Temp> visit(VarDeclNode node) {
        var p = switch (node.varType) {
            case StandardTypeNode t ->
                // 標準型変数宣言
                    node.varNames.stream().map(name -> new Decl(name, false)).toList();
            case ArrayTypeNode t ->
                // 配列型変数宣言
                    node.varNames.stream().map(name -> new DeclArray(name, t.upperBound, t.lowerBound, false)).toList();
            default -> throw new IllegalStateException("Unexpected value: " + node.varType);
        };
        currentScope.addAll(p);
        return Optional.empty();
    }

    @Override
    public Optional<Temp> visit(SubProgDeclNode node) {
        IrFunction func = IrFunction.empty(node.header.name, 1);
        program.functions.put(node.header.name, func);
        currentScope = func;
        visitAll(node);
        currentScope = program.main;
        return Optional.empty();
    }

    @Override
    public Optional<Temp> visit(SubProgHeaderNode node) {
        currentScope.add(new Label("FUNC:"+node.name));
        visitAll(node);
        return Optional.empty();
    }

    @Override
    public Optional<Temp> visit(FormalParamNode node) {
        var p = switch (node.type) {
            case StandardTypeNode t ->
                // 標準型変数宣言
                    node.params.stream().map(name -> new Decl(name, true)).toList();
            case ArrayTypeNode t ->
                // 配列型変数宣言
                    node.params.stream().map(name -> new DeclArray(name, t.upperBound, t.lowerBound, true)).toList();
            default -> throw new IllegalStateException("Unexpected value: " + node.type);
        };
        currentScope.addAll(p);
        return Optional.empty();
    }

    @Override
    public Optional<Temp> visit(ArrayTypeNode node) {
        return Optional.empty();
    }

    @Override
    public Optional<Temp> visit(StandardTypeNode node) {
        return Optional.empty();
    }

    @Override
    public Optional<Temp> visit(CompoundStmtNode node) {
        visitAll(node);
        return Optional.empty();
    }

    @Override
    public Optional<Temp> visit(AssignmentNode node) {
        node.var.accept(this);
        var exprTemp = node.expr.accept(this).get();
        switch (node.var) {
            case StandardVarNode v -> {
                currentScope.add(new Store(Variable.of(v.name), exprTemp));
            }
            case IndexedVarNode v -> {
                var indexTemp = v.index.accept(this).get();
                currentScope.add(new StoreIndexed(Variable.of(v.name), indexTemp, exprTemp));
            }
            default -> throw new IllegalStateException("Unexpected value: " + node.var);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Temp> visit(CallNode node) {
        var args = node.args.stream().map(arg -> arg.accept(this).get()).toList();
        currentScope.add(new Call("FUNC:"+node.name, args));
        return Optional.empty();
    }

    @Override
    public Optional<Temp> visit(WriteNode node) {
        for(var expr : node.exprs) {
            var exprTemp = expr.accept(this).get();
            Type type = expr.getType().get();
            if (type == INTEGER) {
                currentScope.add(new Write(exprTemp, Write.WriteType.INT));
            } else if (type == CHAR) {
                currentScope.add(new Write(exprTemp, Write.WriteType.CHAR));
            } else if (type instanceof ArrayType arrayType && arrayType.elementType == CHAR) {
                currentScope.add(new Write(exprTemp, Write.WriteType.STRING));
            } else {
                throw new IllegalStateException("Unexpected value: " + type);
            }
        }
        currentScope.add(new Write());
        return Optional.empty();
    }

    @Override
    public Optional<Temp> visit(ReadNode node) {
        visitAll(node);
        return Optional.empty();
    }

    @Override
    public Optional<Temp> visit(IfNode node) {
        var thenLabel = labelManager.New();
        var endLabel = labelManager.New();

        var condition = node.condition.accept(this).get();
        currentScope.add(new Jump(thenLabel, condition, Jump.Type.TRUE));

        if(node.elseBranch != null) {
            node.elseBranch.accept(this);
        }

        currentScope.add(new Jump(endLabel));

        currentScope.add(Label.of(thenLabel));
        node.thenBranch.accept(this);

        currentScope.add(Label.of(endLabel));
        return Optional.empty();
    }

    @Override
    public Optional<Temp> visit(WhileNode node) {
        var startLabel = labelManager.New();
        var endLabel = labelManager.New();

        currentScope.add(Label.of(startLabel));
        var condition = node.condition.accept(this).get();
        currentScope.add(new Jump(endLabel, condition, Jump.Type.FALSE));
        node.body.accept(this);
        currentScope.add(Label.of(endLabel));
        return Optional.empty();
    }

    @Override
    public Optional<Temp> visit(BinaryOpNode node) {
        visitAll(node);
        var leftTemp = node.left.accept(this).get();
        var rightTemp = node.right.accept(this).get();
        var op = switch (node.op) {
            case EQ -> BinaryOp.OpType.EQ;
            case GT ->  BinaryOp.OpType.GT;
            case LT ->  BinaryOp.OpType.LT;
            case OR ->  BinaryOp.OpType.OR;
            case ADD ->  BinaryOp.OpType.ADD;
            case AND ->  BinaryOp.OpType.AND;
            case DIV ->  BinaryOp.OpType.DIV;
            case GTE ->  BinaryOp.OpType.GTE;
            case LTE ->  BinaryOp.OpType.LTE;
            case MOD ->  BinaryOp.OpType.MOD;
            case MUL ->  BinaryOp.OpType.MUL;
            case NEQ ->  BinaryOp.OpType.NEQ;
            case SUB ->  BinaryOp.OpType.SUB;
        };
        currentScope.add(new BinaryOp(op, leftTemp, rightTemp));
        return Optional.of(leftTemp);
    }

    @Override
    public Optional<Temp> visit(ConstNode node) {
        visitAll(node);
        Temp temp = Temp.of(tempManager.New());
        Const constValue;
        Type type = node.getType().get();
        if (type == INTEGER) {
            constValue = new Const(Integer.parseInt(node.value));
        } else if (type == Type.BOOLEAN) {
            constValue = new Const(Boolean.parseBoolean(node.value));
        } else if (type == CHAR) {
            constValue = new Const(node.value.charAt(0));
        } else if (type instanceof ArrayType arrayType && arrayType.elementType == CHAR) {
            constValue = new Const(node.value);
        } else {
            throw new IllegalStateException("Unexpected value: " + type);

        }
        currentScope.add(new Move(temp, constValue));
        return Optional.of(temp);
    }

    @Override
    public Optional<Temp> visit(UnaryOpNode node) {
        var operandTemp = node.operand.accept(this).get();
        switch(node.op) {
            case PLUS -> {
                Temp temp = Temp.of(tempManager.New());
                currentScope.add(new Move(temp, operandTemp));
                return Optional.of(temp);
            }
            case MINUS -> {
                Temp temp = Temp.of(tempManager.New());
                currentScope.add(new Move(temp, Const.ZERO));
                currentScope.add(new BinaryOp(BinaryOp.OpType.SUB, temp, operandTemp));
                return Optional.of(temp);
            }
            case NOT ->  {
                Temp temp = Temp.of(tempManager.New());
                currentScope.add(new Move(temp, Const.MASK_16));
                currentScope.add(new BinaryOp(BinaryOp.OpType.XOR, temp, operandTemp));
                return Optional.of(temp);
            }
            default -> throw new IllegalStateException("Unexpected value: " + node.op);
        }
    }

    @Override
    public Optional<Temp> visit(StandardVarNode node) {
        visitAll(node);
        Temp temp = Temp.of(tempManager.New());
        currentScope.add(new Load(temp, Variable.of(node.name)));
        return Optional.of(temp);
    }

    @Override
    public Optional<Temp> visit(IndexedVarNode node) {
        var index = node.index.accept(this).get();
        Temp temp = Temp.of(tempManager.New());
        currentScope.add(new LoadIndexed(temp, Variable.of(node.name), index));
        return Optional.of(temp);
    }
}

