package enshud.s4.compiler.generator.impls.casl.tir.program;

import enshud.s4.compiler.generator.impls.casl.tir.instructions.Instr;

import java.util.List;

public class TIRFunction {
    public String name;
    public FrameInfo frameInfo;

    private List<Instr> instructions;

    public TIRFunction(String name, FrameInfo frameInfo, List<Instr> instructions) {
        this.name = name;
        this.frameInfo = frameInfo;
        this.instructions = instructions;
    }

    public List<Instr> getInstructions() {
        return this.instructions;
    }
}
