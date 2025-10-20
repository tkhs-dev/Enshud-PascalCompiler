package enshud.s4.compiler.ir;

public class DeclArray extends Ir {
    public final String varName;
    public final int upperBound;
    public final int lowerBound;

    public DeclArray(String varName, int upperBound, int lowerBound) {
        this.varName = varName;
        this.upperBound = upperBound;
        this.lowerBound = lowerBound;
    }

    @Override
    public <T> T accept(IIrVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.format("Decl: %s [%d..%d]", varName, lowerBound, upperBound);
    }
}
