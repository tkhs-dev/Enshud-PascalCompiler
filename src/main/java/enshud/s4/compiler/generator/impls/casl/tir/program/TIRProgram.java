package enshud.s4.compiler.generator.impls.casl.tir.program;

import java.util.Map;

public class TIRProgram {
    public final TIRFunction main;

    public final Map<String, TIRFunction> functions;
    public final TIRFunction data;
    public TIRProgram(TIRFunction main, Map<String, TIRFunction> functions, TIRFunction data) {
        this.main = main;
        this.functions = functions;
        this.data = data;
    }
}
