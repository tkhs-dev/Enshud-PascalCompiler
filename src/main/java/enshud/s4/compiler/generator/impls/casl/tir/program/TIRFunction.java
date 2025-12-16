package enshud.s4.compiler.generator.impls.casl.tir.program;

import enshud.s4.compiler.generator.BasicBlock;
import enshud.s4.compiler.generator.impls.casl.tir.instructions.TIrInstr;
import enshud.s4.compiler.instruction.FunctionUnit;

import java.util.List;

public class TIRFunction extends FunctionUnit<TIrInstr<?>> {
    public String name;
    public FrameInfo frameInfo;
    public List<ConstData<?>> constDataList;

    private List<BasicBlock<TIrInstr<?>>> blocks;

    public TIRFunction(String name, FrameInfo frameInfo, List<TIrInstr<?>> instructions, List<ConstData<?>> constDataList) {
        super(name, instructions);
        this.name = name;
        this.frameInfo = frameInfo;
        this.constDataList = constDataList;
        calculateBasicBlocks();
    }

    public List<BasicBlock<TIrInstr<?>>> getBlocks() {
        return blocks;
    }

    private void calculateBasicBlocks() {
        this.blocks = CFGBuilder.build(getInstructions());
    }

    @Override
    protected void onChange() {
        calculateBasicBlocks();
    }
}
