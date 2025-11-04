package enshud.s4.compiler.generator.location;

public class MemoryLocation extends Location {
    public final String baseAddress;
    public final int offset;

    public MemoryLocation(String baseAddress, int offset) {
        this.baseAddress = baseAddress;
        this.offset = offset;
    }

    public MemoryLocation(String address) {
        this.baseAddress = address;
        this.offset = 0;
    }

    @Override
    public String toString() {
        return "Memory(" + baseAddress + (offset != 0 ? " + " + offset : "") + ")";
    }
}
