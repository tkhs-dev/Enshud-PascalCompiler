package enshud.s4.compiler.generator.impls.casl.tir.program;

import enshud.s4.compiler.generator.BasicBlock;
import enshud.s4.compiler.generator.impls.casl.tir.instructions.Instr;
import enshud.s4.compiler.generator.impls.casl.tir.operand.RegisterOperand;

import java.util.List;

public class TIRFunction {
    public String name;
    public FrameInfo frameInfo;

    private List<BasicBlock<Instr<?>>> blocks;

    public TIRFunction(String name, FrameInfo frameInfo, List<BasicBlock<Instr<?>>> blocks) {
        this.name = name;
        this.frameInfo = frameInfo;
        this.blocks = blocks;
    }

    public List<BasicBlock<Instr<?>>> getBlocks() {
        return blocks;
    }

    public List<Instr<?>> getInstructions() {
        return blocks.stream()
                .flatMap(block -> block.getInstructions().stream())
                .toList();
    }
}
