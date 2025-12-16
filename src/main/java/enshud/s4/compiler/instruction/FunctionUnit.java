package enshud.s4.compiler.instruction;

import java.util.List;

public abstract class FunctionUnit<T extends Instruction > {
    private final String name;
    private final List<T> instructions;

    public FunctionUnit(String name, List<T> instructions) {
        this.name = name;
        this.instructions = instructions;
    }

    public String getName() {
        return name;
    }

    public List<T> getInstructions() {
        return List.copyOf(instructions);
    }

    List<T> getMutableInstructions() {
        return instructions;
    }

    public FunctionEditor<T> getEditor() {
        return new FunctionEditor<>(this, this::onChange);
    }

    protected abstract void onChange();
}
