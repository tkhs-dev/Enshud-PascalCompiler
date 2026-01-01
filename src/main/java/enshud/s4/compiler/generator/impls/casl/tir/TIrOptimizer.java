package enshud.s4.compiler.generator.impls.casl.tir;

import enshud.s4.compiler.generator.impls.casl.tir.instructions.Ld;
import enshud.s4.compiler.generator.impls.casl.tir.instructions.St;
import enshud.s4.compiler.generator.impls.casl.tir.instructions.TIrInstr;
import enshud.s4.compiler.generator.impls.casl.tir.operand.VirtualRegister;
import enshud.s4.compiler.generator.impls.casl.tir.program.TIRProgram;
import enshud.s4.compiler.instruction.FunctionUnit;
import enshud.s4.compiler.optimizer.OptimizerPipeline;
import enshud.s4.compiler.optimizer.pass.OptimizePass;

import java.util.ArrayList;

public class TIrOptimizer {
    public void optimize(TIRProgram program) {
        OptimizerPipeline<TIrInstr<?>> pipeline = new OptimizerPipeline<>();
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

    private void initializePasses(OptimizerPipeline<TIrInstr<?>> pipeline) {
        // 最適化パスの追加はここで行う
        pipeline.addPass(new RemoveUnnecessaryLoadStorePass());
        pipeline.addPass(new RemoveUnnecessaryVirtualRegister());
    }
}

// 連続する不要なロード・ストア命令をメモリへのストアと仮想レジスタの移動に置き換えるパス
class RemoveUnnecessaryLoadStorePass implements OptimizePass<TIrInstr<?>> {
    @Override
    public boolean apply(FunctionUnit<TIrInstr<?>> program) {
        boolean optimized = false;
        var editor = program.getEditor();
        while(editor.hasNext()){
            var instr = editor.next();
            if(instr instanceof St st){
                var nextInstr = editor.peekNext();
                if(nextInstr instanceof Ld ld){
                    if(ld.getSrc().equals(st.getDest())){
                        // 不要なストア命令を削除
                        // ロード命令を仮想レジスタの移動に書き換え
                        editor.replace(new Ld(ld.getDest(), st.getSrc()));
                        optimized = true;
                    }
                }
            }
        }

        return optimized;
    }
}

class RemoveUnnecessaryVirtualRegister implements OptimizePass<TIrInstr<?>> {
    @Override
    public boolean apply(FunctionUnit<TIrInstr<?>> program) {
        boolean optimized = false;
        var editor = program.getEditor();
        VirtualRegister replace = null;
        VirtualRegister newReg = null;
        while(editor.hasNext()){
            var instr = editor.next();
            if(instr instanceof Ld ld){
                // dest,srcが事前割り当て済みの場合は対象外
                if(ld.getSrc() instanceof VirtualRegister src && ld.getDest() instanceof VirtualRegister dest && dest.getPredefined().isEmpty() && src.getPredefined().isEmpty()){
                    replace = dest;
                    newReg = src;
                    optimized = true;
                    editor.prev();
                    editor.remove();
                }
            }
            if(replace != null){
                if(instr.isRegisterUsed(replace)){
                    instr.replaceUse(replace, newReg);
                }
                if(instr.isRegisterDefined(replace)) {
                    instr.replaceDef(replace, newReg);
                }
            }
        }
        return optimized;
    }
}