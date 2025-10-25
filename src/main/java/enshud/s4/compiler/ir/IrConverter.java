package enshud.s4.compiler.ir;

import enshud.pascal.ast.IAstVisitor;
import enshud.pascal.ast.nodes.*;
import enshud.pascal.ast.nodes.expr.*;
import enshud.pascal.ast.nodes.stmt.*;
import enshud.s4.compiler.ir.operand.Const;
import enshud.s4.compiler.ir.operand.Temp;

import java.util.Optional;

public class IrConverter {
    public static IrProgram convert(AstNode astProgram) {
        IrConverterVisitor visitor = new IrConverterVisitor();
        astProgram.accept(visitor);
        return visitor.getInstructions();
    }
}

class IrConverterVisitor implements IAstVisitor<Optional<Temp>> {
    private final SerialManager tempManager;
    private final SerialManager labelManager;
    private IrProgram currentScope;
    private final IrProgram mainSegment;
    private final IrProgram functionSegment;
    private final IrProgram dataSegment;

    public IrConverterVisitor() {
        this.tempManager = new SerialManager("t");
        this.labelManager = new SerialManager("L");
        this.mainSegment = new IrProgram();
        this.functionSegment = new IrProgram();
        this.dataSegment = new IrProgram();
    }

    private void visitAll(AstNode node) {
        node.getChildren().forEach(child -> child.accept(this));
    }

    public IrProgram getInstructions() {
        return IrProgram.merge(mainSegment, functionSegment, dataSegment);
    }

    @Override
    public Optional<Temp> visit(ProgramNode node) {
        currentScope = mainSegment;
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
                    node.varNames.stream().map(Decl::new).toList();
            case ArrayTypeNode t ->
                // 配列型変数宣言
                    node.varNames.stream().map(name -> new DeclArray(name, t.upperBound, t.lowerBound)).toList();
            default -> throw new IllegalStateException("Unexpected value: " + node.varType);
        };
        currentScope.addAll(p);
        return Optional.empty();
    }

    @Override
    public Optional<Temp> visit(SubProgDeclNode node) {
        currentScope = functionSegment;
        visitAll(node);
        currentScope = mainSegment;
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
                    node.params.stream().map(Decl::new).toList();
            case ArrayTypeNode t ->
                // 配列型変数宣言
                    node.params.stream().map(name -> new DeclArray(name, t.upperBound, t.lowerBound)).toList();
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
                currentScope.add(new Move(Temp.of(v.name), exprTemp));
            }
            case IndexedVarNode v -> {
                var indexTemp = v.index.accept(this).get();
                currentScope.add(new StoreIndexed(v.name, indexTemp, exprTemp));
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
        var exprTemp = node.exprs.stream().map(expr -> expr.accept(this).get()).toList();
        currentScope.add(new Writeln(exprTemp));
        return Optional.empty();
    }

    @Override
    public Optional<Temp> visit(ReadNode node) {
        visitAll(node);
        return Optional.empty();
    }

    @Override
    public Optional<Temp> visit(IfNode node) {
        var elseLabel = node.elseBranch != null ? labelManager.New() : null;
        var thenLabel = labelManager.New();
        var endLabel = labelManager.New();

        var condition = node.condition.accept(this).get();
        currentScope.add(new Jump(thenLabel, condition, Jump.TYPE_TRUE));

        if(node.elseBranch != null) {
            currentScope.add(Label.of(elseLabel));
            node.elseBranch.accept(this);return Optional.empty();
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
        currentScope.add(new Jump(endLabel, condition, Jump.TYPE_FALSE));
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
        currentScope.add(new Move(temp, new Const(node.value)));
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
        currentScope.add(new Move(temp, Temp.of(node.name)));
        return Optional.of(temp);
    }

    @Override
    public Optional<Temp> visit(IndexedVarNode node) {
        var index = node.index.accept(this).get();
        Temp temp = Temp.of(tempManager.New());
        currentScope.add(new LoadIndexed(temp, node.name, index));
        return Optional.of(temp);
    }
}

class SerialManager {
    private int counter;
    private final String prefix;

    public SerialManager(String prefix) {
        this.counter = 0;
        this.prefix = prefix;
    }

    public String New(){
        counter++;
        return prefix + counter;
    }
}
