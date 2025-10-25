package enshud.s4.compiler.optimizer.pass;

import enshud.s4.compiler.ir.IrProgram;

public interface IrPass {
    boolean apply(IrProgram program);
}
