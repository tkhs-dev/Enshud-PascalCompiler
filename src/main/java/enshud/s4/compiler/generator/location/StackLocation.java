package enshud.s4.compiler.generator.location;

public class StackLocation extends Location {
    public final int offset;
    public final int inFrameOffset;

    public StackLocation(int offset, int inFrameOffset) {
        this.offset = offset;
        this.inFrameOffset = inFrameOffset;
    }

    public StackLocation(int offset) {
        this(offset, 0);
    }

    @Override
    public String toString() {
        return "Stack(offset=" + offset + ")";
    }
}
