package enshud.s4.compiler.ir;

import enshud.s4.compiler.ir.operand.Operand;
import enshud.s4.compiler.ir.operand.Temp;

public class LoadIndexed extends Ir {
    public final Temp targetVar;
    public final String arrayVar;
    public final Operand indexVar;

    public LoadIndexed(Temp targetVar, String arrayVar, Operand indexVar) {
        this.targetVar = targetVar;
        this.arrayVar = arrayVar;
        this.indexVar = indexVar;
    }

    @Override
    public <T> T accept(IIrVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.format("Load: %s <- %s[%s]", targetVar, arrayVar, indexVar);
    }
}
