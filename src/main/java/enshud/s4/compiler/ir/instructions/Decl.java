package enshud.s4.compiler.ir.instructions;

import enshud.s4.compiler.ir.IIrVisitor;
import enshud.s4.compiler.ir.operand.Temp;

import java.util.List;

public class Decl extends ITInstruction {
    public final String varName;
    public final boolean isParam;

    public Decl(String varName, boolean isParam) {
        this.varName = varName;
        this.isParam = isParam;
    }

    @Override
    public <T> T accept(IIrVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.format("Decl: %s", varName);
    }
}
