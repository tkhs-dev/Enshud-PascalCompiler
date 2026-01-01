package enshud.s4.compiler.instruction;

import java.util.Collection;
import java.util.Collections;
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

    public T peekNext() {
        T next = iterator.next();
        iterator.previous();
        return next;
    }

    public boolean hasPrev() {
        return iterator.hasPrevious();
    }

    public T prev() {
        current = iterator.previous();
        return current;
    }

    public T peekPrev() {
        T prev = iterator.previous();
        iterator.next();
        return prev;
    }

    public T current() {
        return current;
    }

    public void applyToCurrent(java.util.function.Consumer<T> consumer) {
        if (current != null) {
            consumer.accept(current);
            onChange.run();
        }
    }

    public void applyAt(int index, java.util.function.Consumer<T> consumer) {
        T instr = instructions.get(index);
        consumer.accept(instr);
        onChange.run();
    }

    @SafeVarargs
    public final void insertBefore(boolean labelTransfer, T... instr) {
        if (labelTransfer && current != null && instr.length > 0) {
            instr[0].setLabel(current.getLabel().orElse(null));
            current.clearLabel();
        }
        for (T i : instr) {
            iterator.previous();
            iterator.add(i);
        }
        for (int i = 0; i < instr.length; i++) {
            iterator.next();
        }
        onChange.run();
    }

    @SafeVarargs
    public final void insertBefore(T... instr) {
        insertBefore(true, instr);
    }

    @SafeVarargs
    public final void insertAfter(boolean labelTransfer, T... instr) {
        if (labelTransfer && current != null && instr.length > 0) {
            instr[instr.length - 1].setLabel(current.getLabel().orElse(null));
            current.clearLabel();
        }
        for (T i : instr) {
            iterator.add(i);
        }
        onChange.run();
    }

    @SafeVarargs
    public final void insertAfter(T... instr) {
        insertAfter(false, instr);
    }

    @SafeVarargs
    public final void replace(boolean labelTransfer, T... instr) {
        if (labelTransfer && current != null && instr.length > 0) {
            instr[0].setLabel(current.getLabel().orElse(null));
            current.clearLabel();
        }
        this.remove();
        for (T i : instr) {
            iterator.add(i);
        }
        onChange.run();
    }

    @SafeVarargs
    public final void replace(T... instr) {
        replace(true, instr);
    }

    @SafeVarargs
    public final void append(boolean labelTransfer, T... instr) {
        if (labelTransfer && instr.length > 0 && !instructions.isEmpty()) {
            T last = instructions.getLast();
            instr[0].setLabel(last.getLabel().orElse(null));
            last.clearLabel();
        }
        Collections.addAll(instructions, instr);
        onChange.run();
    }

    @SafeVarargs
    public final void append(T... instr) {
        append(false, instr);
    }

    @SafeVarargs
    public final void prepend(boolean labelTransfer, T... instr) {
        if (labelTransfer && instr.length > 0 && !instructions.isEmpty()) {
            T first = instructions.getFirst();
            instr[0].setLabel(first.getLabel().orElse(null));
            first.clearLabel();
        }
        instructions.addAll(0, List.of(instr));
        onChange.run();
    }

    @SafeVarargs
    public final void prepend(T... instr) {
        prepend(true, instr);
    }

    public void appendAll(boolean labelTransfer, Collection<T> instrs) {
        if (labelTransfer && !instrs.isEmpty() && !instructions.isEmpty()) {
            T last = instructions.getLast();
            T first = instrs.iterator().next();
            first.setLabel(last.getLabel().orElse(null));
            last.clearLabel();
        }
        instructions.addAll(instrs);
        onChange.run();
    }

    public void appendAll(Collection<T> instrs) {
        appendAll(false, instrs);
    }

    public void appendAll(boolean labelTransfer, InstructionSet<T> instrSet) {
        appendAll(labelTransfer, instrSet.toList());
    }

    public void appendAll(InstructionSet<T> instrSet) {
        appendAll(false, instrSet.toList());
    }

    public void prependAll(boolean labelTransfer, Collection<T> instrs) {
        if (labelTransfer && !instrs.isEmpty() && !instructions.isEmpty()) {
            T first = instructions.getFirst();
            T instrFirst = instrs.iterator().next();
            instrFirst.setLabel(first.getLabel().orElse(null));
            first.clearLabel();
        }
        instructions.addAll(0, instrs);
        onChange.run();
    }

    public void prependAll(Collection<T> instrs) {
        prependAll(true, instrs);
    }

    public void prependAll(boolean labelTransfer, InstructionSet<T> instrSet) {
        prependAll(labelTransfer, instrSet.toList());
    }

    public void prependAll(InstructionSet<T> instrSet) {
        prependAll(true, instrSet.toList());
    }

    public void remove(boolean labelTransfer) {
        if (labelTransfer && current != null) {
            if (this.hasNext()) {
                T next = this.next();
                next.setLabel(current.getLabel().orElse(null));
                this.prev();
            } else if (this.hasPrev()) {
                T prev = this.prev();
                prev.setLabel(current.getLabel().orElse(null));
                this.next();
            }
        }
        iterator.remove();
        onChange.run();
    }

    public void remove() {
        remove(true);
    }

    public void resetToStart() {
        while (this.hasPrev()) {
            this.prev();
        }
        current = null;
    }

    public void resetToEnd() {
        while (this.hasNext()) {
            this.next();
        }
        current = null;
    }

    public InstructionSet<T> getSubsequentInstructions() {
        if (current == null) {
            return InstructionSet.empty();
        }
        int currentIndex = iterator.previousIndex();
        return new InstructionSet<>(instructions.subList(currentIndex + 1, instructions.size()));
    }
}
