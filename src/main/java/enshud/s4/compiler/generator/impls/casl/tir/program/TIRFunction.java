package enshud.s4.compiler.generator.impls.casl.tir.program;

import enshud.s4.compiler.generator.BasicBlock;
import enshud.s4.compiler.generator.impls.casl.tir.instructions.TIrInstr;

import java.util.List;

public class TIRFunction {
    public String name;
    public FrameInfo frameInfo;

    private List<BasicBlock<TIrInstr<?>>> blocks;

    public TIRFunction(String name, FrameInfo frameInfo, List<BasicBlock<TIrInstr<?>>> blocks) {
        this.name = name;
        this.frameInfo = frameInfo;
        this.blocks = blocks;
    }

    public List<BasicBlock<TIrInstr<?>>> getBlocks() {
        return blocks;
    }

    public List<TIrInstr<?>> getInstructions() {
        return blocks.stream()
                .flatMap(block -> block.getInstructions().stream())
                .toList();
    }
}
