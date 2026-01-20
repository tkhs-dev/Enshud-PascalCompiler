package enshud.s4.compiler.generator.impls.cproc.assembly.instructions;

import enshud.s4.compiler.generator.impls.cproc.assembly.operand.RegisterOperand;

public class SUB extends OperationalInstr<SUB>{
    public SUB(RegisterOperand dest) {
        super(dest);
    }
}
