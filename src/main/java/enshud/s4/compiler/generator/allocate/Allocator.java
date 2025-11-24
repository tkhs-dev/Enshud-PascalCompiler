package enshud.s4.compiler.generator.allocate;

import java.util.Objects;

public abstract class Allocator {
    protected TargetDescription targetDescription;

    public Allocator(TargetDescription targetDescription){
        this.targetDescription = Objects.requireNonNull(targetDescription);
    }

    public abstract Allocation allocate(InterferenceGraph graph);
}
