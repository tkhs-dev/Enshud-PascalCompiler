package enshud.s4.compiler.generator.impls.casl.tir.program;

import enshud.s4.compiler.generator.BasicBlock;
import enshud.s4.compiler.generator.impls.casl.tir.instructions.TIrInstr;
import enshud.s4.compiler.generator.impls.casl.tir.instructions.Jmp;
import enshud.s4.compiler.generator.impls.casl.tir.instructions.Ret;

import java.util.*;

public class CFGBuilder {
    public static List<BasicBlock<TIrInstr<?>>> build(List<TIrInstr<?>> instructions) {
        List<BasicBlock<TIrInstr<?>>> blocks = new ArrayList<>();
        Map<String, BasicBlock<TIrInstr<?>>> labelToBlock = new HashMap<>();

        // ブロックの分割とラベルのマッピング
        List<TIrInstr<?>> instrs = new ArrayList<>();
        for (TIrInstr<?> instr : instructions) {
            if (instr.getLabel().isPresent() && !instrs.isEmpty()) {
                // ラベルで始まる命令が現れた場合、現在の命令リストをブロックとして切り出す
                var block = new BasicBlock<>(instrs);
                blocks.add(block);
                if (instrs.getFirst().getLabel().isPresent()) {
                    labelToBlock.put(instrs.getFirst().getLabel().get(), block);
                }
                instrs = new ArrayList<>();
            }
            instrs.add(instr);
            if (instr instanceof Jmp) {
                var block = new BasicBlock<>(instrs);
                blocks.add(block);
                if (instrs.size() > 1 && instrs.getFirst().getLabel().isPresent()) {
                    labelToBlock.put(instrs.getFirst().getLabel().get(), block);
                }
                instrs = new ArrayList<>();
            }
            if (instr instanceof Ret){
                var block = new BasicBlock<>(instrs);
                blocks.add(block);
                if (instrs.size() > 1 && instrs.getFirst().getLabel().isPresent()) {
                    labelToBlock.put(instrs.getFirst().getLabel().get(), block);
                }
                instrs = new ArrayList<>();
            }
        }
        if (!instrs.isEmpty()) {
            var block = new BasicBlock<>(instrs);
            blocks.add(block);
            if (instrs.getFirst().getLabel().isPresent()) {
                labelToBlock.put(instrs.getFirst().getLabel().get(), block);
            }
        }

        // ラベルの関連付け
        for (BasicBlock<TIrInstr<?>> block : blocks) {
            var lastInstr = block.getInstructions().getLast();
            if (lastInstr instanceof Jmp jmp) {
                String targetLabel = jmp.addressLabel;
                var targetBlock = labelToBlock.get(targetLabel);
                if (targetBlock != null) {
                    block.addSuccessor(targetBlock);
                    targetBlock.addPredecessor(block);
                }
                if (jmp.type != Jmp.Type.UNCONDITIONAL) {
                    // 条件分岐命令の場合、次のブロックも後続ブロックになる
                    int currentIndex = blocks.indexOf(block);
                    if (currentIndex + 1 < blocks.size()) {
                        BasicBlock<TIrInstr<?>> nextBlock = blocks.get(currentIndex + 1);
                        block.addSuccessor(nextBlock);
                        nextBlock.addPredecessor(block);
                    }
                }
            }else if(lastInstr instanceof Ret){
                // Ret命令の場合、後続ブロックはない
            }else{
                // 最後の命令がジャンプ命令でない場合、次のブロックが後続ブロックになる
                int currentIndex = blocks.indexOf(block);
                if (currentIndex + 1 < blocks.size()) {
                    BasicBlock<TIrInstr<?>> nextBlock = blocks.get(currentIndex + 1);
                    block.addSuccessor(nextBlock);
                    nextBlock.addPredecessor(block);
                }
            }
        }
        return blocks;
    }
}
