package enshud.s4.compiler.generator.allocator;

import java.util.Optional;

public class MemoryLocation extends Location {
    public final int baseAddress;
    public final Optional<String> offset;

    public MemoryLocation(int baseAddress, String offsetRegister) {
        this.baseAddress = baseAddress;
        this.offset = Optional.of(offsetRegister);
    }

    public MemoryLocation(int address) {
        this.baseAddress = address;
        this.offset = Optional.empty();
    }
}
