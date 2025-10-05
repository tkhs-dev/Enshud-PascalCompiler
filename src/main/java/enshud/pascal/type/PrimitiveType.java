package enshud.pascal.type;

public class PrimitiveType extends Type {
    private final String name;

    public PrimitiveType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
