package enshud.pascal.type;

public class ArrayType extends Type {
    public final int lowerBound;
    public final int upperBound;
    public final Type elementType;

    public ArrayType(int lowerBound, int upperBound, Type elementType) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.elementType = elementType;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ArrayType)) return false;
        ArrayType other = (ArrayType) obj;
        return this.lowerBound == other.lowerBound &&
                this.upperBound == other.upperBound &&
                this.elementType.equals(other.elementType);
    }
}
