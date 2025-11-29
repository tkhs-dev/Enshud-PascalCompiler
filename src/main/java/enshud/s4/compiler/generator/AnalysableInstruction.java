package enshud.s4.compiler.generator;

import java.util.Set;

public abstract class AnalysableInstruction<T> implements UseDef<T> {
    public Set<T> liveIn;
    public Set<T> liveOut;
}
