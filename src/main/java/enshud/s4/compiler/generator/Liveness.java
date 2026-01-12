package enshud.s4.compiler.generator;

import enshud.s4.compiler.instruction.BasicBlock;
import enshud.s4.compiler.instruction.Instruction;
import enshud.s4.compiler.instruction.UseDef;

import java.util.Map;
import java.util.Set;

public class Liveness<R, T extends Instruction & UseDef<R>> {
    private final Map<T, Set<R>> liveIn;
    private final Map<T, Set<R>> liveOut;
    private final Map<BasicBlock<T>, Set<R>> blockLiveIn;
    private final Map<BasicBlock<T>, Set<R>> blockLiveOut;

    public Liveness(
            Map<T, Set<R>> liveIn,
            Map<T, Set<R>> liveOut,
            Map<BasicBlock<T>, Set<R>> blockLiveIn,
            Map<BasicBlock<T>, Set<R>> blockLiveOut
    ) {
        this.liveIn = liveIn;
        this.liveOut = liveOut;
        this.blockLiveIn = blockLiveIn;
        this.blockLiveOut = blockLiveOut;
    }

    public Set<R> getLiveIn(AnalysableInstruction instr) {
        return liveIn.get(instr);
    }

    public Set<R> getLiveOut(AnalysableInstruction instr) {
        return liveOut.get(instr);
    }

    public Set<R> getLiveIn(BasicBlock<? extends AnalysableInstruction> block) {
        return blockLiveIn.get(block);
    }

    public Set<R> getLiveOut(BasicBlock<? extends AnalysableInstruction> block) {
        return blockLiveOut.get(block);
    }
}
