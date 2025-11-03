package enshud.s4.compiler.generator.allocator;

import java.util.Optional;

public class MemoryLocation extends Location {
    public final String baseAddress;
    public final Optional<Integer> offset;

    public MemoryLocation(String baseAddress, int offset) {
        this.baseAddress = baseAddress;
        this.offset = Optional.of(offset);
    }

    public MemoryLocation(String address) {
        this.baseAddress = address;
        this.offset = Optional.empty();
    }

    @Override
    public String toString() {
        return offset.map(s -> String.format("[%s + %d]", baseAddress, s))
                     .orElseGet(() -> String.format("[%s]", baseAddress));
    }
}
