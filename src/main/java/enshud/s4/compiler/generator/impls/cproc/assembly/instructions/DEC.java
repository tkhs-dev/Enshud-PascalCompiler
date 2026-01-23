package enshud.s4.compiler.generator.impls.cproc.assembly.instructions;

import enshud.s4.compiler.generator.impls.cproc.assembly.operand.RegisterOperand;

public class DEC extends OperationalInstr<DEC> {
    public DEC(RegisterOperand dest) {
        super(dest);
    }
}
