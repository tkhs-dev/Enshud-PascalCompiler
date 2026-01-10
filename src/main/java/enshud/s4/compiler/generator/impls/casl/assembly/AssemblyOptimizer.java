package enshud.s4.compiler.generator.impls.casl.assembly;

import enshud.s4.compiler.generator.impls.casl.assembly.instructions.AssemblyInstr;
import enshud.s4.compiler.generator.impls.casl.assembly.instructions.LD;
import enshud.s4.compiler.instruction.InstructionSet;
import enshud.s4.compiler.optimizer.OptimizerPipeline;
import enshud.s4.compiler.optimizer.pass.OptimizePass;

public class AssemblyOptimizer {
    public void optimize(InstructionSet<AssemblyInstr<?>> program) {
        OptimizerPipeline<InstructionSet<AssemblyInstr<?>>> pipeline = new OptimizerPipeline<>();
        initializePasses(pipeline);

        pipeline.run(program, true);
    }

    private void initializePasses(OptimizerPipeline<InstructionSet<AssemblyInstr<?>>> pipeline) {
        // 最適化パスの追加はここで行う
        pipeline.addPass(new RemoveRedundantCopyPass());
    }
}

class RemoveRedundantCopyPass implements OptimizePass<InstructionSet<AssemblyInstr<?>>> {
    @Override
    public boolean apply(InstructionSet<AssemblyInstr<?>> program) {
        boolean optimized = false;
        var editor = program.getEditor();
        while(editor.hasNext()){
            var instr = editor.next();
            if(instr instanceof LD ld && ld.op1.equals(ld.op2)){
                editor.prev();
                editor.remove();
                optimized = true;
            }
        }
        return optimized;
    }
}
