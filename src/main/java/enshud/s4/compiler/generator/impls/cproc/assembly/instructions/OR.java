package enshud.s4.compiler.generator.impls.cproc.assembly.instructions;

import enshud.s4.compiler.generator.impls.cproc.assembly.operand.RegisterOperand;

public class OR extends OperationalInstr<OR>{
    public OR(RegisterOperand dest) {
        super(dest);
    }
}
