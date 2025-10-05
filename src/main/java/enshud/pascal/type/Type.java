package enshud.pascal.type;

public abstract class Type {
    public static final Type INTEGER = new PrimitiveType("integer");
    public static final Type CHAR = new PrimitiveType("char");
    public static final Type BOOLEAN = new PrimitiveType("boolean");
}
