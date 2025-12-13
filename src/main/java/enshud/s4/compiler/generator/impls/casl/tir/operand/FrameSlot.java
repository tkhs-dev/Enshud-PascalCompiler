package enshud.s4.compiler.generator.impls.casl.tir.operand;

public class FrameSlot extends TIROperand {
    public final int slot;
    public TIROperand offset = null;
    public final int levelDiff;

    public FrameSlot(int slot, int levelDiff) {
        this.slot = slot;
        this.levelDiff = levelDiff;
    }

    public FrameSlot setOffset(TIROperand offset) {
        this.offset = offset;
        return this;
    }

    @Override
    public String toString() {
        return "FrameSlot(slot=" + slot + ", levelDiff=" + levelDiff + (offset != null ? ", offset=" + offset : "") + ")";
    }
}
