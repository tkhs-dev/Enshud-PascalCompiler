package enshud.s4.compiler.optimizer.pass;

import enshud.s4.compiler.ir.program.IrFunction;

public interface IrPass {
    boolean apply(IrFunction program);
}
