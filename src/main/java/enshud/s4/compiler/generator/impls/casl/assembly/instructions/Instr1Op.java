package enshud.s4.compiler.generator.impls.casl.assembly.instructions;

import enshud.s4.compiler.generator.impls.casl.assembly.operand.AssemblyOperand;

public abstract class Instr1Op<Op1 extends AssemblyOperand> extends AssemblyInstr<Instr1Op<Op1>> {
    protected Op1 op;

    public Instr1Op(Op1 op1) {
        this.op = op1;
    }

    @Override
    public String toString() {
        return String.format("%s %s", super.toString(), op);
    }
}
