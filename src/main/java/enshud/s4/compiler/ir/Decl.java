package enshud.s4.compiler.ir;

public class Decl extends Ir {
    public final String varName;

    public Decl(String varName) {
        this.varName = varName;
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
