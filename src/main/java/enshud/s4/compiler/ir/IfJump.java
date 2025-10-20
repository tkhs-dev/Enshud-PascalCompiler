package enshud.s4.compiler.ir;

import enshud.s4.compiler.ir.operand.Operand;

public class IfJump extends Ir {
    public final Operand condition;
    public final String target;

    public IfJump(Operand condition, String target) {
        this.condition = condition;
        this.target = target;
    }

    @Override
    public <T> T accept(IIrVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.format("If %s Goto %s", condition, target);
    }
}
