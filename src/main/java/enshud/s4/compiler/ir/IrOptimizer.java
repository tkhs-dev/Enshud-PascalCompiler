package enshud.s4.compiler.ir;

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
//        pipeline.addPass(new ConstantFoldingPass());
//        pipeline.addPass(new ConstantPropagationPass());
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
                        case AND -> (Integer) leftConst.value & (Integer) rightConst.value;
                        case OR  -> (Integer) leftConst.value | (Integer) rightConst.value;
                        case EQ  -> leftConst.value.equals(rightConst.value);
                        case NEQ -> !leftConst.value.equals(rightConst.value);
                        case LT  -> (Integer) leftConst.value < (Integer) rightConst.value;
                        case LTE -> (Integer) leftConst.value <= (Integer) rightConst.value;
                        case GT  -> (Integer) leftConst.value > (Integer) rightConst.value;
                        case GTE -> (Integer) leftConst.value >= (Integer) rightConst.value;
                    };
                    constantValues.put(binOp.result.get(), result);
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
        return instr.isBranch() || instr.isTerminal() || instr instanceof Call || instr.getLabel().isPresent();
    }
}
