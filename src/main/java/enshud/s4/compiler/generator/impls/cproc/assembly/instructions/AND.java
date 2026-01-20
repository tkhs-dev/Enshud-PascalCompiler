package enshud.s4.compiler.generator.impls.cproc.assembly.instructions;

import enshud.s4.compiler.generator.impls.cproc.assembly.operand.RegisterOperand;

public class AND extends OperationalInstr<AND>{
    public AND(RegisterOperand dest) {
        super(dest);
    }
}
