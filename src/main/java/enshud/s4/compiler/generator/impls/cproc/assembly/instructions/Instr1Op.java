package enshud.s4.compiler.generator.impls.cproc.assembly.instructions;

import enshud.s4.compiler.generator.impls.cproc.assembly.operand.AssemblyOperand;
import enshud.s4.compiler.instruction.OperandRef;

public abstract class Instr1Op<T, Op extends AssemblyOperand> extends AssemblyInstr<T> {
    protected OperandRef<Op> op;

    public Instr1Op(Op op) {
        this.op = new OperandRef<>(op);
    }

    @Override
    public String toString() {
        return String.format("%s %s", super.toString(), op.toString());
    }
}
