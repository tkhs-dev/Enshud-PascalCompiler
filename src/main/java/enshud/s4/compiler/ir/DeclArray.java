package enshud.s4.compiler.ir;

public class DeclArray extends Ir {
    public final String varName;
    public final int size;

    public DeclArray(String varName, int size) {
        this.varName = varName;
        this.size = size;
    }

    @Override
    public <T> T accept(IIrVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
