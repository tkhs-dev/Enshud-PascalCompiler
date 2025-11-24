package enshud.s4.compiler.generator.impls.casl.assembly.instructions;

public abstract class Instr {
    protected String label = null;

    public void setLabel(String label){
        this.label = label;
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
