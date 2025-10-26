package enshud.s4.compiler.optimizer.pass;

import enshud.s4.compiler.ir.IrFunction;

public interface IrPass {
    boolean apply(IrFunction program);
}
