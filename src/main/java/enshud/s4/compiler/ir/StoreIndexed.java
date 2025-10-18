package enshud.s4.compiler.ir;

public class StoreIndexed extends Ir {
    public final Ir array;
    public final Ir index;
    public final Ir value;

    public StoreIndexed(Ir array, Ir index, Ir value) {
        this.array = array;
        this.index = index;
        this.value = value;
    }

    @Override
    public <T> T accept(IIrVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
