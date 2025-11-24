package enshud.s4.compiler.generator.impls.casl.assembly.instructions;

import enshud.s4.compiler.generator.impls.casl.assembly.operand.OperandKind;

public abstract class Instr2Op<Op1 extends OperandKind,Op2 extends OperandKind> extends Instr {
    protected Op1 op1;
    protected Op2 op2;

    public Instr2Op(Op1 op1, Op2 op2) {
        this.op1 = op1;
        this.op2 = op2;
    }

    @Override
    public String toString() {
        if (op2 == null) {
            return String.format("%s %s", super.toString(), op1);
        } else if (op1 == null) {
            return String.format("%s %s", super.toString(), op2);
        } else {
            return String.format("%s %s, %s", super.toString(), op1, op2);
        }
    }
}
