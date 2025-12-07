package enshud.s4.compiler.generator.impls.casl.tir.operand;

public class FrameSlot extends TIROperand {
    public final int slot;
    public TIROperand offset = null;

    public FrameSlot(int slot) {
        this.slot = slot;
    }

    public FrameSlot setOffset(TIROperand offset) {
        this.offset = offset;
        return this;
    }

    @Override
    public String toString() {
        return String.format("Frame[%d] + %s", slot, offset != null ? offset.toString() : "0");
    }
}
