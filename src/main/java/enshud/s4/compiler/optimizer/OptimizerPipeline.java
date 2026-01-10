package enshud.s4.compiler.optimizer;

import enshud.s4.compiler.instruction.FunctionUnit;
import enshud.s4.compiler.instruction.Instruction;
import enshud.s4.compiler.ir.program.IrFunction;
import enshud.s4.compiler.optimizer.pass.OptimizePass;

import java.util.List;

public class OptimizerPipeline<T> {
    private List<OptimizePass<T>> passes;

    public OptimizerPipeline() {
        passes = new java.util.ArrayList<>();
    }

    public void addPass(OptimizePass<T> pass) {
        passes.add(pass);
    }

    private boolean runOnce(T program) {
        boolean optimized = false;
        for (OptimizePass<T> pass : passes) {
            boolean passOptimized = pass.apply(program);
            if (passOptimized) {
                optimized = true;
            }
        }
        return optimized;
    }

    public void run(T program, boolean repeatUntilNoChange) {
        if (repeatUntilNoChange) {
            boolean optimized;
            do {
                optimized = runOnce(program);
            } while (optimized);
        } else {
            runOnce(program);
        }
    }
}
