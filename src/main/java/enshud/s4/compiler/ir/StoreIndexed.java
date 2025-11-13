package enshud.s4.compiler.ir;

import enshud.s4.compiler.ir.operand.Operand;
import enshud.s4.compiler.ir.operand.Symbol;
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
    public List<? extends Symbol> getUsedSymbols() {
        List<Symbol> symbols = new ArrayList<>();
        symbols.add(target);
        if (index instanceof Symbol idxSymbol) {
            symbols.add(idxSymbol);
        }
        if (value instanceof Symbol valSymbol) {
            symbols.add(valSymbol);
        }
        return symbols;
    }

    @Override
    public String toString() {
        return String.format("Store: %s[%s] <- %s", target.name, index, value);
    }
}
