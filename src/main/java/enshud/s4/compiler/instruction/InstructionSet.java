package enshud.s4.compiler.instruction;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

public class InstructionSet<T extends Instruction> implements Iterable<T> {
    private final List<T> instructions;

    public InstructionSet() {
        this.instructions = new java.util.ArrayList<>();
    }

    InstructionSet(List<T> instructions) {
        this.instructions = instructions;
    }

    public InstructionSetEditor<T> getEditor(Runnable onChange) {
        return new InstructionSetEditor<>(instructions, onChange);
    }

    public InstructionSetEditor<T> getEditor() {
        return new InstructionSetEditor<>(instructions, () -> {});
    }

    public List<T> toList() {
        return new java.util.ArrayList<>(instructions);
    }

    public static <T extends Instruction> InstructionSet<T> of(List<T> instructions) {
        return new InstructionSet<>(instructions);
    }

    public static <T extends Instruction> InstructionSet<T> empty() {
        return new InstructionSet<>(new java.util.ArrayList<>());
    }

    @Override
    public Iterator<T> iterator() {
        return instructions.iterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        instructions.forEach(action);
    }

    @Override
    public Spliterator<T> spliterator() {
        return instructions.spliterator();
    }
}
