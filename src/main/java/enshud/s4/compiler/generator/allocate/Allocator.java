package enshud.s4.compiler.generator.allocate;

import enshud.s4.compiler.generator.InterferenceGraph;

import java.util.Objects;

public abstract class Allocator {
    protected InterferenceGraph graph;
    protected TargetDescription target;

    public Allocator(InterferenceGraph graph, TargetDescription targetDescription) {
        this.graph = Objects.requireNonNull(graph);
        this.target = Objects.requireNonNull(targetDescription);
    }

    public abstract Allocation allocate();
}
