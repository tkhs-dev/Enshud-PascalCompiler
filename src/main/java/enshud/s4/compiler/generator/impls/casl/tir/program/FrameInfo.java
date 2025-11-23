package enshud.s4.compiler.generator.impls.casl.tir.program;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FrameInfo {
    public int localVarSize;
    public int paramSize;

    private Map<String, FrameSlot> slotMap = new HashMap<>();
    private int nextSlot;

    public FrameInfo() {
        this.localVarSize = 0;
        this.paramSize = 0;
        this.nextSlot = 0;
    }

    public int addLocalVar(String name, int size) {
        localVarSize += size;
        return addSlot(name, size, FrameSlot.Kind.LOCAL);
    }

    public int addParam(String name, int size) {
        paramSize += size;
        return addSlot(name, size, FrameSlot.Kind.PARAM);
    }

    private int addSlot(String name, int size, FrameSlot.Kind kind) {
        slotMap.put(name, new FrameSlot(nextSlot, size, kind));
        return nextSlot++;
    }

    public Optional<FrameSlot> getSlot(String name) {
        return Optional.ofNullable(slotMap.get(name));
    }

    public static class FrameSlot {
        public int slot;
        public int size;
        public Kind kind;

        public FrameSlot(int slot, int size, Kind kind) {
            this.slot = slot;
            this.size = size;
            this.kind = kind;
        }

        public enum Kind {
            LOCAL,
            PARAM
        }
    }
}
