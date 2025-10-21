package enshud.pascal.type;

public class PrimitiveType extends Type {
    private final String name;

    public PrimitiveType(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof PrimitiveType other) {
            System.out.println("Comparing " + this.name + " with " + other.name);
            return this.name.equals(other.name);
        } else {
            System.out.println("Object type is " + obj.getClass().getName() + ", not PrimitiveType");
            return false;
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
