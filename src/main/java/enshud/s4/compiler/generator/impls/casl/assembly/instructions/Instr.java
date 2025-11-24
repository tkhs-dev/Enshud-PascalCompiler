package enshud.s4.compiler.generator.impls.casl.assembly.instructions;

public abstract class Instr<T extends Instr<T>> {
    protected String label = null;

    public T setLabel(String label){
        this.label = label;
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
            return getInstructionName();
        }
    }
}
