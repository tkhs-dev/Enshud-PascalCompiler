package enshud.s4.compiler.ir;

import enshud.pascal.ast.IAstVisitor;
import enshud.pascal.ast.nodes.*;
import enshud.pascal.ast.nodes.expr.*;
import enshud.pascal.ast.nodes.stmt.*;
import enshud.pascal.type.ArrayType;
import enshud.pascal.type.Type;
import enshud.s4.compiler.instruction.InstructionSetEditor;
import enshud.s4.compiler.ir.instructions.*;
import enshud.s4.compiler.ir.operand.Const;
import enshud.s4.compiler.ir.operand.Operand;
import enshud.s4.compiler.ir.operand.Temp;
import enshud.s4.compiler.ir.operand.Variable;
import enshud.s4.compiler.ir.program.IrFunction;
import enshud.s4.compiler.ir.program.IrProgram;
import enshud.util.SerialManager;

import java.util.HashMap;
import java.util.Map;
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

class IrConverterVisitor implements IAstVisitor<Optional<Operand>> {
    private final SerialManager tempManager;
    private final SerialManager labelManager;
    private final SerialManager funcManager;

    private IrFunction currentScope;
    private InstructionSetEditor<ITInstruction> currentEditor;

    private final IrFunction main;
    private Map<String, IrFunction> functions;
    private final Map<String, String> funcLabelMap;

    public IrConverterVisitor() {
        this.tempManager = new SerialManager("t");
        this.labelManager = new SerialManager("L");
        this.funcManager = new SerialManager("F");
        this.main = IrFunction.empty("main", 0, null);
        this.functions = new HashMap<>();
        this.funcLabelMap = new HashMap<>();
    }

    private void visitAll(AstNode node) {
        node.getChildren().forEach(child -> child.accept(this));
    }

    public IrProgram getProgram() {
        return new IrProgram(main, functions);
    }

    private void setCurrent(IrFunction function) {
        this.currentScope = function;
        this.currentEditor = function.getEditor();
    }

    @Override
    public Optional<Operand> visit(ProgramNode node) {
        setCurrent(main);
        visitAll(node);
        return Optional.empty();
    }

    @Override
    public Optional<Operand> visit(BlockNode node) {
        visitAll(node);
        return Optional.empty();
    }

    @Override
    public Optional<Operand> visit(VarDeclNode node) {
        var type = node.getType();
        if (type.isEmpty()) {
            throw new IllegalStateException("Type is not set for VarDeclNode");
        }
        node.varNames.forEach(varName -> {
            currentScope.addVariable(varName, type.get());
        });
        return Optional.empty();
    }

    @Override
    public Optional<Operand> visit(SubProgDeclNode node) {
        IrFunction func = IrFunction.empty(node.header.name, 1, main);
        functions.put(node.header.name, func);
        setCurrent(func);
        visitAll(node);
        setCurrent(main);
        return Optional.empty();
    }

    @Override
    public Optional<Operand> visit(SubProgHeaderNode node) {
        var label = funcManager.New();
        funcLabelMap.put(node.name, label);
        currentEditor.append(Label.of(label));
        currentScope.label = label;
        visitAll(node);
        return Optional.empty();
    }

    @Override
    public Optional<Operand> visit(FormalParamNode node) {
        var type = node.getType();
        if (type.isEmpty()) {
            throw new IllegalStateException("Type is not set for FormalParamNode");
        }
        node.params.forEach(paramName -> {
            currentScope.addParameter(paramName, type.get());
        });
        return Optional.empty();
    }

    @Override
    public Optional<Operand> visit(ArrayTypeNode node) {
        return Optional.empty();
    }

    @Override
    public Optional<Operand> visit(StandardTypeNode node) {
        return Optional.empty();
    }

    @Override
    public Optional<Operand> visit(CompoundStmtNode node) {
        visitAll(node);
        return Optional.empty();
    }

    @Override
    public Optional<Operand> visit(AssignmentNode node) {
//        node.var.accept(this);
        var exprTemp = node.expr.accept(this).get();
        switch (node.var) {
            case StandardVarNode v -> {
                currentEditor.append(new Store(Variable.of(v.name), exprTemp));
            }
            case IndexedVarNode v -> {
                var indexTemp = v.index.accept(this).get();
                currentEditor.append(new StoreIndexed(Variable.of(v.name), indexTemp, exprTemp));
            }
            default -> throw new IllegalStateException("Unexpected value: " + node.var);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Operand> visit(CallNode node) {
        var args = node.args.stream().map(arg -> arg.accept(this).get()).toList();
        var label = funcLabelMap.get(node.name);
        if (label == null) {
            throw new IllegalStateException("Function label not found for function: " + node.name);
        }
        currentEditor.append(new Call(label, args));
        return Optional.empty();
    }

    @Override
    public Optional<Operand> visit(WriteNode node) {
        for(var expr : node.exprs) {
            var exprTemp = expr.accept(this).get();
            Type type = expr.getType().get();
            if (type == INTEGER) {
                currentEditor.append(new Write(exprTemp, Write.WriteType.INT));
            } else if (type == CHAR) {
                currentEditor.append(new Write(exprTemp, Write.WriteType.CHAR));
            } else if (type instanceof ArrayType arrayType && arrayType.elementType == CHAR) {
                currentEditor.append(new Write(exprTemp, Write.WriteType.STRING));
            } else {
                throw new IllegalStateException("Unexpected value: " + type);
            }
        }
        currentEditor.append(new Write());
        return Optional.empty();
    }

    @Override
    public Optional<Operand> visit(ReadNode node) {
        visitAll(node);
        return Optional.empty();
    }

    @Override
    public Optional<Operand> visit(IfNode node) {
        var thenLabel = labelManager.New();
        var endLabel = labelManager.New();

        var condition = node.condition.accept(this).get();
        currentEditor.append(new Jump(thenLabel, condition, Jump.Type.TRUE));

        if(node.elseBranch != null) {
            node.elseBranch.accept(this);
        }

        currentEditor.append(new Jump(endLabel));

        currentEditor.append(Label.of(thenLabel));
        node.thenBranch.accept(this);

        currentEditor.append(Label.of(endLabel));
        return Optional.empty();
    }

    @Override
    public Optional<Operand> visit(WhileNode node) {
        var startLabel = labelManager.New();
        var endLabel = labelManager.New();

        currentEditor.append(Label.of(startLabel));
        var condition = node.condition.accept(this).get();
        currentEditor.append(new Jump(endLabel, condition, Jump.Type.FALSE));
        node.body.accept(this);
        currentEditor.append(new Jump(startLabel));
        currentEditor.append(Label.of(endLabel));
        return Optional.empty();
    }

    @Override
    public Optional<Operand> visit(BinaryOpNode node) {
        visitAll(node);
        var resultTemp = Temp.of(tempManager.New());
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
        currentEditor.append(new BinaryOp(op, resultTemp, leftTemp, rightTemp));
        return Optional.of(resultTemp);
    }

    @Override
    public Optional<Operand> visit(ConstNode node) {
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
        currentEditor.append(new Move(temp, constValue));
        return Optional.of(temp);
    }

    @Override
    public Optional<Operand> visit(UnaryOpNode node) {
        var operandTemp = node.operand.accept(this).get();
        switch(node.op) {
            case PLUS -> {
                Temp temp = Temp.of(tempManager.New());
                currentEditor.append(new Move(temp, operandTemp));
                return Optional.of(temp);
            }
            case MINUS -> {
                Temp temp = Temp.of(tempManager.New());
                currentEditor.append(new BinaryOp(BinaryOp.OpType.SUB, temp, Const.ZERO, operandTemp));
                return Optional.of(temp);
            }
            case NOT ->  {
                Temp temp = Temp.of(tempManager.New());
                currentEditor.append(new BinaryOp(BinaryOp.OpType.XOR, temp, Const.MASK_16, operandTemp));
                return Optional.of(temp);
            }
            default -> throw new IllegalStateException("Unexpected value: " + node.op);
        }
    }

    @Override
    public Optional<Operand> visit(StandardVarNode node) {
        visitAll(node);
        Temp temp = Temp.of(tempManager.New());
        currentEditor.append(new Load(temp, Variable.of(node.name)));
        return Optional.of(temp);
    }

    @Override
    public Optional<Operand> visit(IndexedVarNode node) {
        var index = node.index.accept(this).get();
        Temp temp = Temp.of(tempManager.New());
        currentEditor.append(new LoadIndexed(temp, Variable.of(node.name), index));
        return Optional.of(temp);
    }
}

