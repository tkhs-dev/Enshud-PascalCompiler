package enshud.s4.compiler.generator.impls.casl.assembly.instructions;

import enshud.s4.compiler.generator.impls.casl.assembly.operand.OperandKind;

public class Instr1Op<Op1 extends OperandKind> extends Instr {
    protected Op1 op1;

    public Instr1Op(Op1 op1) {
        this.op1 = op1;
    }

    @Override
    public String toString() {
        return String.format("%s %s", super.toString(), op1);
    }
}
