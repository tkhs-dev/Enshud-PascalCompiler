package enshud.s4.compiler.generator.impls.cproc.assembly.instructions;

import enshud.s4.compiler.generator.impls.cproc.assembly.operand.RegisterOperand;

public class NOT extends OperationalInstr<NOT>{
    public NOT(RegisterOperand dest) {
        super(dest);
    }
}
