package enshud.s4.compiler.generator.impls.cproc.assembly.instructions;

import enshud.s4.compiler.generator.impls.cproc.assembly.operand.RegisterOperand;

public class INC extends OperationalInstr<INC> {
    public INC(RegisterOperand dest) {
        super(dest);
    }
}
