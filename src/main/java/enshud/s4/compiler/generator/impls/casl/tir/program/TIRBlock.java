package enshud.s4.compiler.generator.impls.casl.tir.program;

import enshud.s4.compiler.generator.impls.casl.tir.instructions.Instr;

import java.util.List;

public class TIRBlock {
    private List<Instr> instructions;
    private List<TIRBlock> successors;
    private List<TIRBlock> predecessors;

    public TIRBlock(List<Instr> instructions) {
        this.instructions = instructions;
        this.successors = new java.util.ArrayList<>();
        this.predecessors = new java.util.ArrayList<>();
    }

    public List<Instr> getInstructions() {
        return instructions;
    }

    public List<TIRBlock> getSuccessors() {
        return successors;
    }

    public List<TIRBlock> getPredecessors() {
        return predecessors;
    }

    void addSuccessor(TIRBlock block) {
        this.successors.add(block);
    }

    void addPredecessor(TIRBlock block) {
        this.predecessors.add(block);
    }
}
