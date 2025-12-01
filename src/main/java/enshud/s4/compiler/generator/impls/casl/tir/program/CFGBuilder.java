package enshud.s4.compiler.generator.impls.casl.tir.program;

import enshud.s4.compiler.generator.BasicBlock;
import enshud.s4.compiler.generator.impls.casl.tir.instructions.Instr;
import enshud.s4.compiler.generator.impls.casl.tir.instructions.Jmp;
import enshud.s4.compiler.generator.impls.casl.tir.instructions.Ret;
import enshud.s4.compiler.generator.impls.casl.tir.operand.RegisterOperand;

import java.util.*;

public class CFGBuilder {
    public static List<BasicBlock<RegisterOperand,Instr<?>>> build(List<Instr> instructions) {
        List<BasicBlock<RegisterOperand,Instr<?>>> blocks = new ArrayList<>();
        Map<String, BasicBlock<RegisterOperand,Instr<?>>> labelToBlock = new HashMap<>();

        // ブロックの分割とラベルのマッピング
        List<Instr<?>> instrs = new ArrayList<>();
        for (Instr<?> instr : instructions) {
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
        for (BasicBlock<RegisterOperand,Instr<?>> block : blocks) {
            Instr lastInstr = block.getInstructions().getLast();
            if (lastInstr instanceof Jmp jmp) {
                String targetLabel = jmp.addressLabel;
                BasicBlock<RegisterOperand,Instr<?>> targetBlock = labelToBlock.get(targetLabel);
                if (targetBlock != null) {
                    block.addSuccessor(targetBlock);
                    targetBlock.addPredecessor(block);
                }
                if (jmp.type != Jmp.Type.UNCONDITIONAL) {
                    // 条件分岐命令の場合、次のブロックも後続ブロックになる
                    int currentIndex = blocks.indexOf(block);
                    if (currentIndex + 1 < blocks.size()) {
                        BasicBlock<RegisterOperand,Instr<?>> nextBlock = blocks.get(currentIndex + 1);
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
                    BasicBlock<RegisterOperand,Instr<?>> nextBlock = blocks.get(currentIndex + 1);
                    block.addSuccessor(nextBlock);
                    nextBlock.addPredecessor(block);
                }
            }
        }
        return blocks;
    }
}
