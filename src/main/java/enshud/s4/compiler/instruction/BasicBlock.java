package enshud.s4.compiler.instruction;

import java.util.List;

public class BasicBlock<R extends Instruction> {
    private InstructionSet<R> instructions;
    private List<BasicBlock<R>> successors;
    private List<BasicBlock<R>> predecessors;

    public BasicBlock(InstructionSet<R> instructions) {
        this.instructions = instructions;
        this.successors = new java.util.ArrayList<>();
        this.predecessors = new java.util.ArrayList<>();
    }

    public InstructionSet<R> getInstructions() {
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
}
