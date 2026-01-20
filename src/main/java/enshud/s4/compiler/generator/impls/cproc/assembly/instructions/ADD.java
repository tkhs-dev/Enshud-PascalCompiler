package enshud.s4.compiler.generator.impls.cproc.assembly.instructions;

import enshud.s4.compiler.generator.impls.cproc.assembly.operand.RegisterOperand;

public class ADD extends OperationalInstr<ADD>{
    public ADD(RegisterOperand dest) {
        super(dest);
    }
}
