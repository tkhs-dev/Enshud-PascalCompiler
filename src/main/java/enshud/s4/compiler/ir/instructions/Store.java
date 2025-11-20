package enshud.s4.compiler.ir.instructions;

import enshud.s4.compiler.ir.IIrVisitor;
import enshud.s4.compiler.ir.operand.Operand;
import enshud.s4.compiler.ir.operand.Temp;
import enshud.s4.compiler.ir.operand.Variable;

import java.util.List;

public class Store extends Ir{
    public final Variable target;
    public final Operand value;

    public Store(Variable target, Operand value) {
        this.target = target;
        this.value = value;
    }

    @Override
    public <T> T accept(IIrVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public List<Temp> getUsedTemps() {
        var temps = new java.util.ArrayList<Temp>();
        if (value instanceof Temp temp) {
            temps.add(temp);
        }
        return temps;
    }

    @Override
    public String toString() {
        return String.format("Store: %s <- %s", target.name, value);
    }
}
