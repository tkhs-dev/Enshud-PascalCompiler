package enshud.s4.compiler.ir;

import enshud.s4.compiler.ir.operand.Symbol;

import java.util.List;

public class Labeled extends Ir {
    public final String label;
    public final Ir ir;

    public Labeled(String label, Ir ir) {
        this.label = label;
        this.ir = ir;
    }

    @Override
    public <T> T accept(IIrVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public List<? extends Symbol> getUsedSymbols() {
        return ir.getUsedSymbols();
    }

    @Override
    public String toString() {
        return String.format("%s  ", label);
    }
}
