package enshud.s4.compiler.ir.instructions;

import enshud.s4.compiler.ir.IIrVisitor;
import enshud.s4.compiler.ir.operand.Operand;
import enshud.s4.compiler.ir.operand.Temp;
import enshud.s4.compiler.ir.operand.Variable;

import java.util.ArrayList;
import java.util.List;

public class LoadIndexed extends Ir {
    public final Temp targetVar;
    public final Variable arrayVar;
    public final Operand indexVar;

    public LoadIndexed(Temp targetVar, Variable arrayVar, Operand indexVar) {
        this.targetVar = targetVar;
        this.arrayVar = arrayVar;
        this.indexVar = indexVar;
    }

    @Override
    public <T> T accept(IIrVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public List<Temp> getUsedTemps() {
        var symbols = new ArrayList<Temp>();
        symbols.add(targetVar);
        if (indexVar instanceof Temp temp) {
            symbols.add(temp);
        }
        return symbols;
    }

    @Override
    public String toString() {
        return String.format("Load: %s <- %s[%s]", targetVar, arrayVar.name, indexVar);
    }
}
