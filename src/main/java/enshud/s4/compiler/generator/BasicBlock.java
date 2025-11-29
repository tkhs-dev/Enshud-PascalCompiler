package enshud.s4.compiler.generator;

import java.util.List;
import java.util.Set;

public class BasicBlock<T, R extends AnalysableInstruction<T>> {
    private List<R> instructions;
    private List<BasicBlock<T, R>> successors;
    private List<BasicBlock<T, R>> predecessors;

    public Set<T> liveIn;
    public Set<T> liveOut;

    public BasicBlock(List<R> instructions) {
        this.instructions = instructions;
        this.successors = new java.util.ArrayList<>();
        this.predecessors = new java.util.ArrayList<>();
    }

    public List<R> getInstructions() {
        return instructions;
    }

    public List<BasicBlock<T, R>> getSuccessors() {
        return successors;
    }

    public List<BasicBlock<T, R>> getPredecessors() {
        return predecessors;
    }

    public void addSuccessor(BasicBlock<T, R> block) {
        this.successors.add(block);
    }

    public void addPredecessor(BasicBlock<T, R> block) {
        this.predecessors.add(block);
    }

    public Set<T> getUse() {
        Set<T> useSet = new java.util.HashSet<>();
        Set<T> defSet = new java.util.HashSet<>();

        for (R instr : instructions) {
            Set<T> instrUses = instr.getUse();
            Set<T> instrDefs = instr.getDef();

            for (T reg : instrUses) {
                if (!defSet.contains(reg)) {
                    useSet.add(reg);
                }
            }

            defSet.addAll(instrDefs);
        }

        return useSet;
    }

    public Set<T> getDef() {
        Set<T> defSet = new java.util.HashSet<>();

        for (R instr : instructions) {
            defSet.addAll(instr.getDef());
        }

        return defSet;
    }
}
