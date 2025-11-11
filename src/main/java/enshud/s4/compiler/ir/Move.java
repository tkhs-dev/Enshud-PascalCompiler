package enshud.s4.compiler.ir;

import enshud.s4.compiler.ir.operand.Operand;
import enshud.s4.compiler.ir.operand.Symbol;

import java.util.ArrayList;
import java.util.List;

public class Move extends Ir {
    public final Symbol dest;
    public final Operand src;

    public Move(Symbol dest, Operand src) {
        this.dest = dest;
        this.src = src;
    }

    @Override
    public <T> T accept(IIrVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public List<Symbol> getUsedTemps() {
        var temps = new ArrayList<Symbol>();
        temps.add(dest);
        if (src instanceof Symbol srcSymbol) {
            temps.add(srcSymbol);
        }
        return temps;
    }

    @Override
    public String toString() {
        return String.format("Move: %s <- %s", dest, src);
    }
}
