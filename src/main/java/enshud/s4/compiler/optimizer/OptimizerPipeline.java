package enshud.s4.compiler.optimizer;

import enshud.s4.compiler.ir.program.IrFunction;
import enshud.s4.compiler.optimizer.pass.IrPass;

import java.util.List;

public class OptimizerPipeline {
    private List<IrPass> passes;

    public OptimizerPipeline() {
        passes = new java.util.ArrayList<>();
    }

    void addPass(IrPass pass) {
        passes.add(pass);
    }

    private boolean runOnce(IrFunction program) {
        boolean optimized = false;
        for (IrPass pass : passes) {
            boolean passOptimized = pass.apply(program);
            if (passOptimized) {
                optimized = true;
            }
        }
        return optimized;
    }

    public void run(IrFunction program, boolean repeatUntilNoChange) {
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
