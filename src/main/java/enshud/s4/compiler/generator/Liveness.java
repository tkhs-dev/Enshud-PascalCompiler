package enshud.s4.compiler.generator;

import enshud.s4.compiler.instruction.BasicBlock;

import java.util.Map;
import java.util.Set;

public class Liveness<R extends AnalysableInstruction> {
    private final Map<R, Set<AllocatableRegister>> liveIn;
    private final Map<R, Set<AllocatableRegister>> liveOut;
    private final Map<BasicBlock<R>, Set<AllocatableRegister>> blockLiveIn;
    private final Map<BasicBlock<R>, Set<AllocatableRegister>> blockLiveOut;

    public Liveness(
            Map<R, Set<AllocatableRegister>> liveIn,
            Map<R, Set<AllocatableRegister>> liveOut,
            Map<BasicBlock<R>, Set<AllocatableRegister>> blockLiveIn,
            Map<BasicBlock<R>, Set<AllocatableRegister>> blockLiveOut
    ) {
        this.liveIn = liveIn;
        this.liveOut = liveOut;
        this.blockLiveIn = blockLiveIn;
        this.blockLiveOut = blockLiveOut;
    }

    public Set<AllocatableRegister> getLiveIn(AnalysableInstruction instr) {
        return liveIn.get(instr);
    }

    public Set<AllocatableRegister> getLiveOut(AnalysableInstruction instr) {
        return liveOut.get(instr);
    }

    public Set<AllocatableRegister> getLiveIn(BasicBlock<? extends AnalysableInstruction> block) {
        return blockLiveIn.get(block);
    }

    public Set<AllocatableRegister> getLiveOut(BasicBlock<? extends AnalysableInstruction> block) {
        return blockLiveOut.get(block);
    }
}
