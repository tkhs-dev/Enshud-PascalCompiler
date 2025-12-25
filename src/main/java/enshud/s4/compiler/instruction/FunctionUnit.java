package enshud.s4.compiler.instruction;

public abstract class FunctionUnit<T extends Instruction > {
    private final String name;
    private final InstructionSet<T> instructions;

    public FunctionUnit(String name, InstructionSet<T> instructions) {
        this.name = name;
        this.instructions = instructions;
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

    protected abstract void onChange();
}
