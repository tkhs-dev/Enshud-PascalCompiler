package enshud.s4.compiler.generator.impls.casl.tir.program;

public abstract class ConstData<T> {
    public final String label;
    public final T value;

    public ConstData(String label, T value) {
        this.label = label;
        this.value = value;
    }
}
