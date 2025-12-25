package enshud.s4.compiler.generator.impls.casl.tir.program;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FrameInfo {
    public int localVarSize;
    public int paramSize;
    public int spillSize;

    private Map<String, FrameSlot> slotMap = new HashMap<>();
    private int nextLocalSlot;
    private int nextParamSlot;

    public FrameInfo() {
        this.localVarSize = 0;
        this.paramSize = 0;
        this.spillSize = 0;
        this.nextLocalSlot = 0;
        this.nextParamSlot = 0;
    }

    public int addLocalVar(String name, int size) {
        localVarSize += size;
        return addSlot(name, size, FrameSlot.Kind.LOCAL);
    }

    public int addParam(String name, int size) {
        paramSize += size;
        return addSlot(name, size, FrameSlot.Kind.PARAM);
    }

    public int addSpill(int size) {
        spillSize += size;
        var slot = nextLocalSlot;
        nextLocalSlot += size;
        return slot;
    }

    private int addSlot(String name, int size, FrameSlot.Kind kind) {
        switch (kind) {
            case LOCAL:
                var localSlot = new FrameSlot(nextLocalSlot, size, kind);
                slotMap.put(name, localSlot);
                nextLocalSlot += size;
                return localSlot.slot;
            case PARAM:
                var paramSlot = new FrameSlot(nextParamSlot, size, kind);
                slotMap.put(name, paramSlot);
                nextParamSlot += size;
                return paramSlot.slot;
            default:
                throw new IllegalArgumentException("Unknown FrameSlot.Kind: " + kind);
        }
    }

    public int getFrameSize() {
        return localVarSize + spillSize;
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
