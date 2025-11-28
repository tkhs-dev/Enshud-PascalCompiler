package enshud.s4.compiler.generator.impls.casl.tir.program;

import enshud.s4.compiler.generator.impls.casl.tir.instructions.Instr;

import java.util.List;

public class TIRFunction {
    public String name;
    public FrameInfo frameInfo;

    private List<TIRBlock> blocks;

    public TIRFunction(String name, FrameInfo frameInfo, List<TIRBlock> blocks) {
        this.name = name;
        this.frameInfo = frameInfo;
        this.blocks = blocks;
    }

    public List<TIRBlock> getBlocks() {
        return blocks;
    }

    public List<Instr> getInstructions() {
        return blocks.stream()
                .flatMap(block -> block.getInstructions().stream())
                .toList();
    }
}
