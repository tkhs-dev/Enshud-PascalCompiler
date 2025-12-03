package enshud.s4.compiler.generator;

import java.util.Set;

public abstract class AnalysableInstruction implements UseDef {
    public Set<AllocatableRegister> liveIn;
    public Set<AllocatableRegister> liveOut;
    public boolean isCall(){
        return false;
    }
}
