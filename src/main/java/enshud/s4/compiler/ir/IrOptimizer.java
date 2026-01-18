package enshud.s4.compiler.ir;

import enshud.s4.compiler.instruction.CFGBuilder;
import enshud.s4.compiler.instruction.OperandRef;
import enshud.s4.compiler.ir.instructions.*;
import enshud.s4.compiler.ir.operand.Const;
import enshud.s4.compiler.ir.operand.Temp;
import enshud.s4.compiler.ir.operand.Variable;
import enshud.s4.compiler.ir.program.IrFunction;
import enshud.s4.compiler.ir.program.IrProgram;
import enshud.s4.compiler.optimizer.OptimizerPipeline;
import enshud.s4.compiler.optimizer.pass.OptimizePass;

import java.util.*;

import static enshud.s4.compiler.ir.instructions.Jump.Type.*;

public class IrOptimizer {
    public void optimize(IrProgram program) {
        OptimizerPipeline<IrFunction> pipeline = new OptimizerPipeline<>();
        initializePasses(pipeline);

        var functions = new ArrayList<>(program.functions.values());
        functions.addFirst(program.main);

        System.out.println("Starting IR Optimization...");
        for (var func : functions) {
            System.out.println("Before Optimization of function: " + func.name);
            for (var instr : func.getInstructions()) {
                System.out.println("  " + instr);
            }
        }

        for (var func : functions) {
            pipeline.run(func, true);
        }
    }

    private void initializePasses(OptimizerPipeline<IrFunction> pipeline) {
        // 最適化パスの追加はここで行う
        pipeline.addPass(new ConstantFoldingPass());
        pipeline.addPass(new ConstantPropagationPass());
        pipeline.addPass(new UseUnconditionalJumpPass());
        pipeline.addPass(new RemoveUnreachableCodePass());
        pipeline.addPass(new RemoveRedundantJumpsPass());
        pipeline.addPass(new RemoveUnusedLabelsPass());
    }
}

class ConstantFoldingPass implements OptimizePass<IrFunction> {
    @Override
    public boolean apply(IrFunction function) {
        // 定数畳み込みの実装
        var editor = function.getEditor();
        boolean optimized = false;

        // 定数情報を収集
        Map<Temp, Object> constantValues = new HashMap<>();
        while (editor.hasNext()) {
            var instr = editor.next();

            var found = false;
            switch(instr){
                case BinaryOp binOp -> {
                    if(!(binOp.left.get() instanceof Const leftConst && binOp.getRight() instanceof Const rightConst)) {
                        break;
                    }
                    Object result = switch (binOp.opType) {
                        case ADD -> (Integer) leftConst.value + (Integer) rightConst.value;
                        case SUB -> (Integer) leftConst.value - (Integer) rightConst.value;
                        case MUL -> (Integer) leftConst.value * (Integer) rightConst.value;
                        case DIV -> (Integer) leftConst.value / (Integer) rightConst.value;
                        case MOD -> (Integer) leftConst.value % (Integer) rightConst.value;
                        case AND -> (Boolean) leftConst.value & (Boolean) rightConst.value;
                        case OR  -> (Boolean)  leftConst.value | (Boolean)  rightConst.value;
                        case EQ  -> leftConst.value.equals(rightConst.value);
                        case NEQ -> !leftConst.value.equals(rightConst.value);
                        case LT  -> cast(leftConst.value) < cast(rightConst.value);
                        case LTE -> cast(leftConst.value) <= cast(rightConst.value);
                        case GT  -> cast(leftConst.value) > cast(rightConst.value);
                        case GTE -> cast(leftConst.value) >= cast(rightConst.value);
                    };
                    constantValues.put(binOp.result.get(), result);
                    found = true;
                }
                case UnaryOp unaryOp -> {
                    if(!(unaryOp.operand.get() instanceof Const operandConst)) {
                        break;
                    }
                    Object result = switch (unaryOp.opType) {
                        case NEG -> -(Integer) operandConst.value;
                        case NOT -> !(Boolean) operandConst.value;
                    };
                    constantValues.put(unaryOp.result.get(), result);
                    found = true;
                }
                case Move move -> {
                    if(move.src.get() instanceof Const c){
                        if(c.value instanceof Integer || c.value instanceof Boolean){
                            constantValues.put(move.dest.get(), c.value);
                            found = true;
                        }
                    }
                }
                default -> {
                    // 他の命令は無視
                }
            }
            if (found) {
                editor.prev();
                editor.remove();
                optimized = true;
            }
        }

        editor.resetToStart();
        // 定数情報を使って命令を置き換え
        while (editor.hasNext()) {
            var instr = editor.next();

            var used = getUsedOperand(instr);
            for (var ref : Objects.requireNonNull(used)) {
                if (ref.get() instanceof Temp temp && constantValues.containsKey(temp)) {
                    Object constValue = constantValues.get(temp);
                    ref.set(new Const(constValue));
                }
            }
        }
        return optimized;
    }

    private int cast(Object obj) {
        if (obj instanceof Integer i) {
            return i;
        }else if (obj instanceof Boolean b) {
            return b ? 1 : 0;
        }
        throw new IllegalArgumentException("Object is not an Integer: " + obj);
    }

    private Set<OperandRef> getUsedOperand(IRInstruction instr) {
        switch (instr){
            case BinaryOp binOp -> {
                return Set.of(binOp.left, binOp.right);
            }
            case Call call -> {
                return new HashSet<>(call.args);
            }
            case Load load -> {
                return Set.of(load.targetVar);
            }
            case LoadIndexed loadIdx -> {
                return Set.of(loadIdx.arrayVar, loadIdx.indexVar);
            }
            case Store store -> {
                return Set.of(store.value);
            }
            case StoreIndexed storeIdx -> {
                return Set.of(storeIdx.value, storeIdx.index);
            }
            case Move move -> {
                return Set.of(move.src);
            }
            case UnaryOp unaryOp -> {
                return Set.of(unaryOp.operand);
            }
            case Write write -> {
                return Set.of(write.operand);
            }
            case Jump jump -> {
                if (jump.condition.get() != null) {
                    return Set.of(jump.condition);
                }
            }
            default -> {}
        }
        return Set.of();
    }
}

class ConstantPropagationPass implements OptimizePass<IrFunction> {
    @Override
    public boolean apply(IrFunction function) {
        // 定数伝播の実装
        var editor = function.getEditor();
        boolean optimized = false;

        Map<Variable, Object> constantValues = new HashMap<>();

        while(editor.hasNext()) {
            var instr = editor.next();

            if(isBreakableInstruction(instr)) {
                constantValues.clear();
                continue;
            }

            if(instr instanceof Store store) {
                if(store.value.get() instanceof Const c) {
                    constantValues.put(store.target.get(), c.value);
                }
            }

            // 定数伝播の適用
            if(instr instanceof Load load) {
                if(constantValues.containsKey(load.var.get())) {
                    Object constValue = constantValues.get(load.var.get());
                    editor.prev();
                    editor.replace(new Move(load.targetVar.get(), new Const(constValue)));
                    optimized = true;
                }
            }
        }

        return optimized;
    }

    private boolean isBreakableInstruction(IRInstruction instr) {
        return instr instanceof Jump || instr.isTerminal() || instr instanceof Call || instr instanceof Label;
    }
}

class UseUnconditionalJumpPass implements OptimizePass<IrFunction> {
    @Override
    public boolean apply(IrFunction function) {
        var editor = function.getEditor();
        boolean optimized = false;

        while(editor.hasNext()) {
            var instr = editor.next();

            if(instr instanceof Jump jump && jump.condition.get() instanceof Const c) {
                if(c.value instanceof Boolean boolValue) {
                    if((boolValue && jump.type == TRUE) || (!boolValue && jump.type == FALSE)) {
                        // 条件が常に真の場合、無条件ジャンプに置き換え
                        editor.prev();
                        editor.replace(new Jump(jump.target, null, UNCONDITIONAL));
                        optimized = true;
                    }else if((!boolValue && jump.type == TRUE) || (boolValue && jump.type == FALSE)) {
                        // 条件が常に偽の場合、ジャンプ命令を削除
                        editor.prev();
                        editor.remove();
                        optimized = true;
                    }
                }
            }
        }

        return optimized;
    }
}

class RemoveUnreachableCodePass implements OptimizePass<IrFunction> {
    @Override
    public boolean apply(IrFunction function) {
        var cfg = CFGBuilder.build(function.getInstructions());
        var removalInstructions = new HashSet<IRInstruction>();
        for (var block : cfg) {
            if (!block.isEntryBlock && block.getPredecessors().isEmpty()){
                removalInstructions.addAll(block.getInstructions().toList());
            }
        }

        var editor = function.getEditor();
        boolean optimized = false;

        if(!removalInstructions.isEmpty()) {
            while(editor.hasNext()) {
                var instr = editor.next();
                if(removalInstructions.contains(instr)) {
                    System.out.println("Removing unreachable instruction: " + instr);
                    editor.prev();
                    editor.remove();
                    optimized = true;
                }
            }
        }

        return optimized;
    }
}

// すぐとなりへのジャンプを削除
class RemoveRedundantJumpsPass implements OptimizePass<IrFunction> {
    @Override
    public boolean apply(IrFunction function) {
        var editor = function.getEditor();
        boolean optimized = false;

        while(editor.hasNext()) {
            var instr = editor.next();

            if(instr instanceof Jump jump) {
                if(editor.hasNext()) {
                    var nextInstr = editor.peekNext();
                    if(nextInstr.getLabel().isPresent() && nextInstr.getLabel().get().equals(jump.target)) {
                        // すぐ隣へのジャンプは不要
                        System.out.println("Removing redundant jump to adjacent label: " + jump);
                        editor.prev();
                        editor.remove();
                        optimized = true;
                    }
                }
            }
        }

        return optimized;
    }
}

// 未使用のラベルを削除
class RemoveUnusedLabelsPass implements OptimizePass<IrFunction> {
    @Override
    public boolean apply(IrFunction function) {
        var usedLabels = new HashSet<String>();
        var editor = function.getEditor();
        boolean optimized = false;

        // 使用されているラベルを収集
        while(editor.hasNext()) {
            var instr = editor.next();
            if(instr instanceof Jump) {
                instr.getBranchTarget().ifPresent(usedLabels::add);
            }else if(instr instanceof Call call) {
                usedLabels.add(call.functionLabel);
            }
        }

        // ラベルが不要な命令を削除
        editor.resetToStart();
        while(editor.hasNext()) {
            var instr = editor.next();
            if(instr instanceof Label labelInstr && !labelInstr.name.startsWith("F")) {
                if(!usedLabels.contains(labelInstr.name)) {
                    System.out.println("Removing unused label: " + labelInstr.name);
                    editor.prev();
                    editor.remove();
                    optimized = true;
                }
            }
        }

        return optimized;
    }
}
