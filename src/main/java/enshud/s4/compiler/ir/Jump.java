package enshud.s4.compiler.ir;

public class Jump extends Ir {
    public final String target;

    public Jump(String target) {
        this.target = target;
    }

    @Override
    public <T> T accept(IIrVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.format("Goto: %s", target);
    }
}
