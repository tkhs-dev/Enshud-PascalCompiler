package enshud.s4.compiler.optimizer.pass;

import enshud.s4.compiler.instruction.FunctionUnit;
import enshud.s4.compiler.instruction.Instruction;

public interface OptimizePass<T extends Instruction> {
    boolean apply(FunctionUnit<T> program);
}
