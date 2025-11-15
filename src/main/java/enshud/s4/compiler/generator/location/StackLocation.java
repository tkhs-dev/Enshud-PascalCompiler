package enshud.s4.compiler.generator.location;

public class StackLocation extends Location {
    public final int offset;
    public final int levelDelta;

    public StackLocation(int offset, int levelDelta) {
        this.offset = offset;
        this.levelDelta = levelDelta;
    }

    public StackLocation(int offset) {
        this(offset, 0);
    }

    @Override
    public String toString() {
        return "Stack(offset=" + offset + ")";
    }
}
