package enshud.s4.compiler.generator.impls.cproc.assembly.instructions;

import enshud.s4.compiler.instruction.Instruction;

public abstract class AssemblyInstr<T> extends Instruction {
    public T withLabel(String label) {
        setLabel(label);
        return (T) this;
    }
}
