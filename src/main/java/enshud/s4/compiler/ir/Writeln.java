package enshud.s4.compiler.ir;

import enshud.s4.compiler.ir.operand.Operand;

import java.util.List;

public class Writeln extends Ir {
    public final List<? extends Operand> operands;
    public Writeln(List<? extends Operand> operands) {
        this.operands = operands;
    }

    @Override
    public <T> T accept(IIrVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.format("Writeln: %s", operands);
    }
}
