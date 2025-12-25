package enshud.s4.compiler.instruction;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

public class InstructionSetEditor<T extends Instruction> {
    private final ListIterator<T> iterator;
    private final List<T> instructions;
    private T current;
    private final Runnable onChange;

    public InstructionSetEditor(List<T> instructions, Runnable onChange) {
        this.instructions = instructions;
        this.iterator = instructions.listIterator();
        this.current = null;
        this.onChange = onChange;
    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

    public T next() {
        current = iterator.next();
        return current;
    }

    public boolean hasPrev() {
        return iterator.hasPrevious();
    }

    public T prev() {
        current = iterator.previous();
        return current;
    }

    public T current() {
        return current;
    }

    public void insertBefore(T instr) {
        iterator.previous();
        iterator.add(instr);
        iterator.next();
        onChange.run();
    }

    public void insertAfter(T instr) {
        iterator.add(instr);
        onChange.run();
    }

    public void replace(T instr) {
        iterator.set(instr);
        onChange.run();
    }

    public void append(T instr) {
        instructions.add(instr);
        onChange.run();
    }

    public void prepend(T instr) {
        instructions.addFirst(instr);
        onChange.run();
    }

    public void appendAll(Collection<T> instrs) {
        instructions.addAll(instrs);
        onChange.run();
    }

    public void prependAll(Collection<T> instrs) {
        instructions.addAll(0, instrs);
        onChange.run();
    }

    public void remove() {
        iterator.remove();
        onChange.run();
    }

    public void resetToStart() {
        while (iterator.hasPrevious()) {
            iterator.previous();
        }
        current = null;
    }

    public void resetToEnd() {
        while (iterator.hasNext()) {
            iterator.next();
        }
        current = null;
    }
}
