package enshud.s4.compiler.generator.impls.cproc.tir.instructions;

import enshud.s4.compiler.generator.impls.cproc.tir.operand.TIROperand;
import enshud.s4.compiler.instruction.OperandRef;


public abstract class Instr1Op<T extends TIRInstr<?>, Op extends TIROperand> extends TIRInstr<T> {
    protected OperandRef<Op> op;

    public Instr1Op(Op op) {
        this.op = new OperandRef<>(op);
    }

    @Override
    public String toString() {
        return String.format("%s %s", super.toString(), op.toString());
    }
}
