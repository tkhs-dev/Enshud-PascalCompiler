package enshud.s4.compiler.ir;

import enshud.s4.compiler.ir.operand.Operand;
import enshud.s4.compiler.ir.operand.Temp;

import java.util.ArrayList;
import java.util.List;

public class Move extends Ir {
    public final Temp dest;
    public final Operand src;

    public Move(Temp dest, Operand src) {
        this.dest = dest;
        this.src = src;
    }

    @Override
    public <T> T accept(IIrVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public List<Temp> getUsedTemps() {
        var temps = new ArrayList<Temp>();
        temps.add(dest);
        if (src instanceof Temp srcTemp) {
            temps.add(srcTemp);
        }
        return temps;
    }

    @Override
    public String toString() {
        return String.format("Move: %s <- %s", dest, src);
    }
}
