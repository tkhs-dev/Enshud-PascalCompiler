package enshud.s4.compiler.generator.impls.casl.tir.operand;

public class Address extends TIROperand{
    public final String base;
    public final TIROperand offset;

    private Address(String base, TIROperand offset) {
        this.base = base;
        this.offset = offset;
    }

    public Address(String base) {
        this(base, null);
    }

    public Address setOffset(TIROperand offset) {
        return new Address(this.base, offset);
    }

    @Override
    public String toString() {
        return offset == null ? String.format("[%s]", base) : String.format("[%s + %s]", base, offset);
    }
}
