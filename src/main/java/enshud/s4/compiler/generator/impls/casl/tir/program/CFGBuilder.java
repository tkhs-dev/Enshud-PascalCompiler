package enshud.s4.compiler.generator.impls.casl.tir.program;

import enshud.s4.compiler.generator.impls.casl.tir.instructions.Instr;
import enshud.s4.compiler.generator.impls.casl.tir.instructions.Jmp;
import enshud.s4.compiler.generator.impls.casl.tir.instructions.Ret;

import java.util.*;

public class CFGBuilder {
    public static List<TIRBlock> build(List<Instr> instructions) {
        List<TIRBlock> blocks = new ArrayList<>();
        Map<String, TIRBlock> labelToBlock = new HashMap<>();

        // ブロックの分割とラベルのマッピング
        List<Instr> instrs = new ArrayList<>();
        for (Instr instr : instructions) {
            if (instr.getLabel().isPresent() && !instrs.isEmpty()) {
                // ラベルで始まる命令が現れた場合、現在の命令リストをブロックとして切り出す
                var block = new TIRBlock(instrs);
                blocks.add(block);
                if (instrs.getFirst().getLabel().isPresent()) {
                    labelToBlock.put((String) instrs.getFirst().getLabel().get(), block);
                }
                instrs = new ArrayList<>();
            }
            instrs.add(instr);
            if (instr instanceof Jmp) {
                var block = new TIRBlock(instrs);
                blocks.add(block);
                if (instrs.size() > 1 && instrs.getFirst().getLabel().isPresent()) {
                    labelToBlock.put((String) instrs.getFirst().getLabel().get(), block);
                }
                instrs = new ArrayList<>();
            }
            if (instr instanceof Ret){
                var block = new TIRBlock(instrs);
                blocks.add(block);
                if (instrs.size() > 1 && instrs.getFirst().getLabel().isPresent()) {
                    labelToBlock.put((String) instrs.getFirst().getLabel().get(), block);
                }
                instrs = new ArrayList<>();
            }
        }
        if (!instrs.isEmpty()) {
            var block = new TIRBlock(instrs);
            blocks.add(block);
            if (instrs.getFirst().getLabel().isPresent()) {
                labelToBlock.put((String) instrs.getFirst().getLabel().get(), block);
            }
        }

        // ラベルの関連付け
        for (TIRBlock block : blocks) {
            Instr lastInstr = block.getInstructions().getLast();
            if (lastInstr instanceof Jmp jmp) {
                String targetLabel = jmp.addressLabel;
                TIRBlock targetBlock = labelToBlock.get(targetLabel);
                if (targetBlock != null) {
                    block.addSuccessor(targetBlock);
                    targetBlock.addPredecessor(block);
                }
                if (jmp.type != Jmp.Type.UNCONDITIONAL) {
                    // 条件分岐命令の場合、次のブロックも後続ブロックになる
                    int currentIndex = blocks.indexOf(block);
                    if (currentIndex + 1 < blocks.size()) {
                        TIRBlock nextBlock = blocks.get(currentIndex + 1);
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
                    TIRBlock nextBlock = blocks.get(currentIndex + 1);
                    block.addSuccessor(nextBlock);
                    nextBlock.addPredecessor(block);
                }
            }
        }

        System.out.println("---- Block ----");
        for (TIRBlock block : blocks) {
            System.out.println("Block:");
            for (Instr instr : block.getInstructions()) {
                System.out.println("  " + instr);
            }
            System.out.print("  Successors: ");
            for (TIRBlock succ : block.getSuccessors()) {
                System.out.print("[" + succ.getInstructions().getFirst() + "] ");
            }
            System.out.println();
            System.out.print("  Predecessors: ");
            for (TIRBlock pred : block.getPredecessors()) {
                System.out.print("[" + pred.getInstructions().getFirst() + "] ");
            }
            System.out.println("\n");
        }

        return blocks;
    }
}
