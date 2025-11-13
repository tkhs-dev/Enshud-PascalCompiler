package enshud.s4.compiler.ir;

import enshud.s4.compiler.ir.operand.Operand;
import enshud.s4.compiler.ir.operand.Symbol;
import enshud.s4.compiler.ir.operand.Variable;

import java.util.ArrayList;
import java.util.List;

public class LoadIndexed extends Ir {
    public final Symbol targetVar;
    public final Variable arrayVar;
    public final Operand indexVar;

    public LoadIndexed(Symbol targetVar, Variable arrayVar, Operand indexVar) {
        this.targetVar = targetVar;
        this.arrayVar = arrayVar;
        this.indexVar = indexVar;
    }

    @Override
    public <T> T accept(IIrVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public List<? extends Symbol> getUsedSymbols() {
        var symbols = new ArrayList<Symbol>();
        symbols.add(targetVar);
        symbols.add(arrayVar);
        if (indexVar instanceof Symbol symbol) {
            symbols.add(symbol);
        }
        return symbols;
    }

    @Override
    public String toString() {
        return String.format("Load: %s <- %s[%s]", targetVar, arrayVar.name, indexVar);
    }
}
