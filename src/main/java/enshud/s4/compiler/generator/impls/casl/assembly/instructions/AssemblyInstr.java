package enshud.s4.compiler.generator.impls.casl.assembly.instructions;

import enshud.s4.compiler.instruction.Instruction;

public abstract class AssemblyInstr<T extends AssemblyInstr<T>> extends Instruction {
    public T withLabel(String label){
        setLabel(label);
        return (T)this;
    }

    public String getInstructionName(){
        return this.getClass().getSimpleName().toUpperCase();
    }

    @Override
    public String toString(){
        if(label != null){
            return String.format("%s\t%s", label, getInstructionName());
        } else {
            return String.format("%s\t%s", "", getInstructionName());
        }
    }
}
