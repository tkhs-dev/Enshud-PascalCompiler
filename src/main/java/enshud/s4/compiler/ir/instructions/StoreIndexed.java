package enshud.s4.compiler.ir.instructions;

import enshud.s4.compiler.ir.IIrVisitor;
import enshud.s4.compiler.ir.operand.Operand;
import enshud.s4.compiler.ir.operand.Temp;
import enshud.s4.compiler.ir.operand.Variable;

import java.util.ArrayList;
import java.util.List;

public class StoreIndexed extends Ir {
    public final Variable target;
    public final Operand index;
    public final Operand value;

    public StoreIndexed(Variable target, Operand index, Operand value) {
        this.target = target;
        this.index = index;
        this.value = value;
    }

    @Override
    public <T> T accept(IIrVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public List<Temp> getUsedTemps() {
        List<Temp> symbols = new ArrayList<>();
        if (index instanceof Temp idxTemp) {
            symbols.add(idxTemp);
        }
        if (value instanceof Temp valTemp) {
            symbols.add(valTemp);
        }
        return symbols;
    }

    @Override
    public String toString() {
        return String.format("Store: %s[%s] <- %s", target.name, index, value);
    }
}
