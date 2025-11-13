package enshud.s4.compiler.ir;

import enshud.s4.compiler.ir.operand.Symbol;
import enshud.s4.compiler.ir.operand.Temp;
import enshud.s4.compiler.ir.operand.Variable;

import java.util.List;

public class Load extends Ir {
    public final Temp targetVar;
    public final Variable var;

    public Load(Temp targetVar, Variable var) {
        this.targetVar = targetVar;
        this.var = var;
    }

    @Override
    public <T> T accept(IIrVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public List<? extends Symbol> getUsedSymbols() {
        return List.of(var, targetVar);
    }

    @Override
    public String toString() {
        return String.format("Load: %s <- %s", targetVar.name, var.name);
    }
}
