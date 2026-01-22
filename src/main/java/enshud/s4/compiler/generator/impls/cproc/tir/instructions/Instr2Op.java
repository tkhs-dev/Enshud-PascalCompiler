package enshud.s4.compiler.generator.impls.cproc.tir.instructions;

import enshud.s4.compiler.generator.impls.cproc.tir.operand.TIROperand;
import enshud.s4.compiler.instruction.OperandRef;

public abstract class Instr2Op<T extends TIRInstr<?>, Op1 extends TIROperand, Op2 extends TIROperand> extends TIRInstr<T> {
    protected final OperandRef<Op1> op1;
    protected final OperandRef<Op2> op2;

    public Instr2Op(Op1 op1, Op2 op2) {
        this.op1 = new OperandRef<>(op1);
        this.op2 = new OperandRef<>(op2);
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", super.toString(), op1.toString(), op2.toString());
    }
}
