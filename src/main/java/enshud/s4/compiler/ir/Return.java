package enshud.s4.compiler.ir;

public class Return extends Ir {
    public Return() {
    }

    @Override
    public <T> T accept(IIrVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "Return";
    }
}
