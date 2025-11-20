package enshud.s4.compiler.generator.impls.casl.tir.operand;

public class FrameSlot extends Operand {
    public final int slot;

    public FrameSlot(int slot) {
        this.slot = slot;
    }
}
