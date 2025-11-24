package enshud.s4.compiler.generator.impls.casl.assembly.instructions;

public class DS extends Instr<DS>{
    public final int size;

    public DS(int size){
        this.size = size;
    }

    @Override
    public String toString(){
        return String.format("%s %d", super.toString(), size);
    }
}
