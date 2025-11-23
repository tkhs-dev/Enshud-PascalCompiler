package enshud.s4.compiler.generator.impls.casl.tir.program;

import enshud.s4.compiler.generator.impls.casl.tir.instructions.TIRInstruction;

import java.util.List;

public class TIRFunction {
    public String name;
    public FrameInfo frameInfo;

    private List<TIRInstruction> instructions;

    public TIRFunction(String name, FrameInfo frameInfo, List<TIRInstruction> instructions) {
        this.name = name;
        this.frameInfo = frameInfo;
        this.instructions = instructions;
    }

    public List<TIRInstruction> getInstructions() {
        return this.instructions;
    }
}
