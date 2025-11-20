package enshud.s4.compiler.generator.impls.casl;

import enshud.s4.compiler.ir.program.IrFunction;
import enshud.s4.compiler.optimizer.pass.IrPass;

class Pass implements IrPass {
    @Override
    public boolean apply(IrFunction program) {
        return false;
    }
}
