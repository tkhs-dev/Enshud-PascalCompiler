package enshud.s4.compiler.generator.impls.casl.tir.operand;

public class VirtualRegister extends RegisterOperand {
    public final String id;
    public final double spillCost;
    public final boolean needsAddressable;

    public VirtualRegister(String id, double spillCost, boolean needsAddressable) {
        this.id = id;
        this.spillCost = spillCost;
        this.needsAddressable = needsAddressable;
    }

    public VirtualRegister(String id, double spillCost) {
        this(id, spillCost, false);
    }

    public VirtualRegister(String id, boolean needsAddressable) {
        this(id, 1.0, needsAddressable);
    }

    public VirtualRegister(String id) {
        this(id, 1.0, false);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        VirtualRegister that = (VirtualRegister) obj;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return id;
    }
}
