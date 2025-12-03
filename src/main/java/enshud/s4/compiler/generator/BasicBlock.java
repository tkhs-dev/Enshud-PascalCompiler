package enshud.s4.compiler.generator;

import java.util.List;
import java.util.Set;

public class BasicBlock<R extends AnalysableInstruction> {
    private List<R> instructions;
    private List<BasicBlock<R>> successors;
    private List<BasicBlock<R>> predecessors;

    public Set<AllocatableRegister> liveIn;
    public Set<AllocatableRegister> liveOut;

    public BasicBlock(List<R> instructions) {
        this.instructions = instructions;
        this.successors = new java.util.ArrayList<>();
        this.predecessors = new java.util.ArrayList<>();
    }

    public List<R> getInstructions() {
        return instructions;
    }

    public List<BasicBlock<R>> getSuccessors() {
        return successors;
    }

    public List<BasicBlock<R>> getPredecessors() {
        return predecessors;
    }

    public void addSuccessor(BasicBlock<R> block) {
        this.successors.add(block);
    }

    public void addPredecessor(BasicBlock<R> block) {
        this.predecessors.add(block);
    }

    public Set<AllocatableRegister> getUse() {
        Set<AllocatableRegister> useSet = new java.util.HashSet<>();
        Set<AllocatableRegister> defSet = new java.util.HashSet<>();

        for (R instr : instructions) {
            var instrUses = instr.getUse();
            var instrDefs = instr.getDef();

            for (var reg : instrUses) {
                if (!defSet.contains(reg)) {
                    useSet.add(reg);
                }
            }

            defSet.addAll(instrDefs);
        }

        return useSet;
    }

    public Set<AllocatableRegister> getDef() {
        Set<AllocatableRegister> defSet = new java.util.HashSet<>();

        for (R instr : instructions) {
            defSet.addAll(instr.getDef());
        }

        return defSet;
    }
}
