package enshud.s4.compiler.ir;

import enshud.s4.compiler.ir.operand.Operand;

public class IfGoto extends Ir {
    public final Operand condition;
    public final Label target;

    public IfGoto(Operand condition, Label target) {
        this.condition = condition;
        this.target = target;
    }

    @Override
    public <T> T accept(IIrVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
