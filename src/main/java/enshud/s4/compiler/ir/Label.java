package enshud.s4.compiler.ir;

public class Label extends Ir {
    public final String name;

    public Label(String name) {
        this.name = name;
    }

    public static Label of(String name) {
        return new Label(name);
    }

    @Override
    public <T> T accept(IIrVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.format("%s:", name);
    }
}
