package enshud.s4.compiler.generator.impls.casl.tir;

import enshud.s4.compiler.generator.impls.casl.tir.instructions.Call;
import enshud.s4.compiler.generator.impls.casl.tir.instructions.Ld;
import enshud.s4.compiler.generator.impls.casl.tir.instructions.St;
import enshud.s4.compiler.generator.impls.casl.tir.instructions.TIrInstr;
import enshud.s4.compiler.generator.impls.casl.tir.operand.FrameSlot;
import enshud.s4.compiler.generator.impls.casl.tir.operand.VirtualRegister;
import enshud.s4.compiler.generator.impls.casl.tir.program.TIRFunction;
import enshud.s4.compiler.generator.impls.casl.tir.program.TIRProgram;
import enshud.s4.compiler.instruction.CFGBuilder;
import enshud.s4.compiler.instruction.FunctionUnit;
import enshud.s4.compiler.instruction.Instruction;
import enshud.s4.compiler.optimizer.OptimizerPipeline;
import enshud.s4.compiler.optimizer.pass.OptimizePass;

import java.util.ArrayList;
import java.util.HashMap;

public class TIrOptimizer {
    public void optimize(TIRProgram program) {
        OptimizerPipeline<TIRFunction> pipeline = new OptimizerPipeline<>();
        initializePasses(pipeline);

        var functions = new ArrayList<>(program.functions.values());
        functions.addFirst(program.main);

        System.out.println("Initial TIR:");
        for (var func : functions) {
            func.getInstructions().forEach(instr -> System.out.println("  "+instr.toString()));
            }

        for (var func : functions) {
            pipeline.run(func, true);
        }
    }

    private void initializePasses(OptimizerPipeline<TIRFunction> pipeline) {
        // 最適化パスの追加はここで行う
        pipeline.addPass(new ReuseFrameSlotOnRegisterPass());
    }
}

// 同一ブロック内で、かつストアを挟まずにロードしている場合、ロードを削除してストア先のレジスタを再利用する
class ReuseFrameSlotOnRegisterPass implements OptimizePass<TIRFunction> {
    @Override
    public boolean apply(TIRFunction function) {
        boolean optimized = false;

        var cfg = CFGBuilder.build(function.getInstructions());
        for (var block : cfg) {
            var tmp = new HashMap<FrameSlot, VirtualRegister>();
            for (var instr : block.getInstructions()) {
                if (instr instanceof St store) {
                    tmp.remove(store.getDest());
                } else if(instr instanceof Call){
                    // Call命令はレジスタを破壊するため、すべてクリアする
                    tmp.clear();
                }else if (instr instanceof Ld load && load.getSrc() instanceof FrameSlot frameslot) {
                    if (tmp.containsKey(frameslot)) {
                        System.out.println("Optimizing redundant load: " + load + " to reuse " + tmp.get(frameslot));
                        load.setSrc(tmp.get(frameslot));
                        optimized = true;
                    } else {
                        tmp.put(frameslot, load.getDest());
                    }
                }
            }
        }
        return optimized;
    }
}