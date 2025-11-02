package enshud.s4.compiler.ir;

import enshud.s4.compiler.ir.operand.Operand;
import enshud.s4.compiler.ir.operand.Temp;

import java.util.ArrayList;
import java.util.List;

public class StoreIndexed extends Ir {
    public final String target;
    public final Operand index;
    public final Operand value;

    public StoreIndexed(String target, Operand index, Operand value) {
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
        List<Temp> temps = new ArrayList<>();
        if (index instanceof Temp idxTemp) {
            temps.add(idxTemp);
        }
        if (value instanceof Temp valTemp) {
            temps.add(valTemp);
        }
        return temps;
    }

    @Override
    public String toString() {
        return String.format("Store: %s[%s] <- %s", target, index, value);
    }
}
