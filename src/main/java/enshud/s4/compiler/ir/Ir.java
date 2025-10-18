package enshud.s4.compiler.ir;

public abstract class Ir {
    public abstract <T> T accept(IIrVisitor<T> visitor);
}
