package enshud.s4.compiler.generator.impls.casl.tir.instructions;

import enshud.s4.compiler.generator.impls.casl.tir.operand.TIROperand;
import enshud.s4.compiler.instruction.OperandRef;

import java.util.Set;

public abstract class Instr1Op<T extends TIrInstr<T>, Op extends TIROperand> extends TIrInstr<T> {
    protected final OperandRef<Op> op;

    public Instr1Op(Op op) {
        this.op = new OperandRef<>(op);
    }

    @Override
    public Set<? extends TIROperand> getOperands() {
        return Set.of(op.get());
    }

    @Override
    public String toString() {
        return formatInstruction(op);
    }
}
