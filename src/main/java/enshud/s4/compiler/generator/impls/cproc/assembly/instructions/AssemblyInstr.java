package enshud.s4.compiler.generator.impls.cproc.assembly.instructions;

import enshud.s4.compiler.instruction.Instruction;

public abstract class AssemblyInstr<T> extends Instruction {
    public T withLabel(String label) {
        setLabel(label);
        return (T) this;
    }

    @Override
    public String toString() {
        if (getLabel().isEmpty()) {
            return String.format("\t%s", this.getClass().getSimpleName());
        } else {
            return String.format("%s:\t%s", getLabel().get(), this.getClass().getSimpleName());
        }
    }
}
