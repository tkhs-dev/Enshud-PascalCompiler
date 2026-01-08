package enshud.s4.compiler.instruction;

import java.util.List;

public abstract class FunctionUnit<T extends Instruction > {
    private final String name;
    private final InstructionSet<T> instructions;

    private List<BasicBlock<T>> blocks;

    public FunctionUnit(String name, InstructionSet<T> instructions) {
        this.name = name;
        this.instructions = instructions;
        calculateBasicBlocks();
    }

    public String getName() {
        return name;
    }

    public InstructionSet<T> getInstructions() {
        return instructions;
    }

    public InstructionSetEditor<T> getEditor() {
        return instructions.getEditor(this::onChange);
    }

    public List<BasicBlock<T>> getBlocks() {
        return blocks;
    }

    private void calculateBasicBlocks() {
        this.blocks = CFGBuilder.build(getInstructions());
    }

    protected void onChange(){
        calculateBasicBlocks();
    }
}
