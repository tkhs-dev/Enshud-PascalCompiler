package enshud.s4.compiler.ir.instructions;

import enshud.s4.compiler.ir.IIrVisitor;
import enshud.s4.compiler.ir.operand.Temp;

import java.util.List;

public class DeclArray extends ITInstruction {
    public final String varName;
    public final int upperBound;
    public final int lowerBound;
    public final boolean isParam;

    public DeclArray(String varName, int upperBound, int lowerBound, boolean isParam) {
        this.varName = varName;
        this.upperBound = upperBound;
        this.lowerBound = lowerBound;
        this.isParam = isParam;
    }

    @Override
    public <T> T accept(IIrVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public List<Temp> getUsedTemps() {
        return List.of();
    }

    @Override
    public String toString() {
        return String.format("Decl: %s [%d..%d]", varName, lowerBound, upperBound);
    }
}
