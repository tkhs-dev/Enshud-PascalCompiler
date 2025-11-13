package enshud.s4.compiler.ir;

import enshud.s4.compiler.ir.operand.Symbol;

import java.util.List;

public class Return extends Ir {
    public Return() {
    }

    @Override
    public <T> T accept(IIrVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public List<? extends Symbol> getUsedSymbols() {
        return List.of();
    }

    @Override
    public String toString() {
        return "Return";
    }
}
