package enshud.s4.compiler.generator.impls.casl.tir.operand;

import java.util.Objects;
import java.util.Optional;

public class FrameSlot extends AddressBasedOperand{
    public final int slot;
    public final int levelDiff;

    public FrameSlot(int slot, int levelDiff) {
        this.slot = slot;
        this.levelDiff = levelDiff;
    }

    public FrameSlot withOffset(VirtualRegister offset) {
        this.setOffset(offset);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FrameSlot frameSlot = (FrameSlot) o;

        if (slot != frameSlot.slot) return false;
        if (levelDiff != frameSlot.levelDiff) return false;
        return Objects.equals(offsetRegister, frameSlot.offsetRegister);
    }

    @Override
    public String toString() {
        return "FrameSlot(slot=" + slot + ", levelDiff=" + levelDiff + (offsetRegister != null ? ", offset=" + offsetRegister : "") + ")";
    }
}
