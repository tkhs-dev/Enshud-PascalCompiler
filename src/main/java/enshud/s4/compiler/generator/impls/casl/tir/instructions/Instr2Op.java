package enshud.s4.compiler.generator.impls.casl.tir.instructions;

import enshud.s4.compiler.generator.impls.casl.tir.operand.TIROperand;
import enshud.s4.compiler.instruction.OperandRef;

import java.util.Set;

public abstract class Instr2Op<T extends TIrInstr<T>, Op1 extends TIROperand, Op2 extends TIROperand> extends TIrInstr<T> {
    protected final OperandRef<Op1> op1;
    protected final OperandRef<Op2> op2;

    public Instr2Op(Op1 op1, Op2 op2) {
        this.op1 = new OperandRef<>(op1);
        this.op2 = new OperandRef<>(op2);
    }

    @Override
    public Set<? extends TIROperand> getOperands() {
        return Set.of(op1.get(), op2.get());
    }

    @Override
    public String toString() {
        return formatInstruction(op1, op2);
    }
}
