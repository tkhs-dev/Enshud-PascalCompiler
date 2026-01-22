package enshud.s4.compiler.generator.impls.cproc.tir.instructions;

import enshud.s4.compiler.generator.impls.cproc.tir.operand.VirtualRegister;

public class Sub extends OperationalInstr<Sub>{
    public Sub(VirtualRegister left, VirtualRegister right) {
        super(left, right);
    }

    @Override
    public <R> R accept(ITIRVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
