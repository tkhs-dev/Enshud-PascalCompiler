package enshud.s4.compiler.ir.instructions;

import enshud.s4.compiler.ir.IIrVisitor;
import enshud.s4.compiler.ir.operand.Operand;
import enshud.s4.compiler.ir.operand.Temp;

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
    public List<Temp> getUsedTemps() {
        return operands.stream().filter(x -> x instanceof Temp).map(x -> (Temp)x).toList();
    }

    @Override
    public String toString() {
        return String.format("Writeln: %s", operands);
    }
}
