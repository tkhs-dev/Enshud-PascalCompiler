package enshud.s4.compiler.ir;

public class Goto extends Ir {
    public final Label target;

    public Goto(Label target) {
        this.target = target;
    }

    @Override
    public <T> T accept(IIrVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
