package enshud.s4.compiler.generator;

import enshud.s4.compiler.instruction.Instruction;

import java.util.Set;

public abstract class AnalysableInstruction extends Instruction implements UseDef {
    public Set<AllocatableRegister> liveIn;
    public Set<AllocatableRegister> liveOut;
    public boolean isCall(){
        return false;
    }
}
