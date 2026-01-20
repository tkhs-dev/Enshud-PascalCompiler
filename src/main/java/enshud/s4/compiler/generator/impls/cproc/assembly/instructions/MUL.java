package enshud.s4.compiler.generator.impls.cproc.assembly.instructions;

import enshud.s4.compiler.generator.impls.cproc.assembly.operand.RegisterOperand;

public class MUL extends OperationalInstr<MUL>{
    public MUL(RegisterOperand dest) {
        super(dest);
    }
}
