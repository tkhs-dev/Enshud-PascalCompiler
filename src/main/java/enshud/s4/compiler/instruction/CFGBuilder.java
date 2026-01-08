package enshud.s4.compiler.instruction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CFGBuilder {
    public static <T extends Instruction> List<BasicBlock<T>> build(InstructionSet<T> instructions) {
        List<BasicBlock<T>> blocks = new ArrayList<>();
        Map<String, BasicBlock<T>> labelToBlock = new HashMap<>();

        // ブロックの分割とラベルのマッピング
        List<T> instrs = new ArrayList<>();
        for (T instr : instructions) {
            if (instr.getLabel().isPresent() && !instrs.isEmpty()) {
                // ラベルで始まる命令が現れた場合、現在の命令リストをブロックとして切り出す
                var block = new BasicBlock<>(InstructionSet.of(instrs));
                blocks.add(block);
                if (instrs.getFirst().getLabel().isPresent()) {
                    labelToBlock.put(instrs.getFirst().getLabel().get(), block);
                }
                instrs = new ArrayList<>();
            }
            instrs.add(instr);
            if (instr.isBranch() || instr.isTerminal()) {
                var block = new BasicBlock<>(InstructionSet.of(instrs));
                blocks.add(block);
                if (instrs.size() > 1 && instrs.getFirst().getLabel().isPresent()) {
                    labelToBlock.put(instrs.getFirst().getLabel().get(), block);
                }
                instrs = new ArrayList<>();
            }
        }
        if (!instrs.isEmpty()) {
            var block = new BasicBlock<>(InstructionSet.of(instrs));
            blocks.add(block);
            if (instrs.getFirst().getLabel().isPresent()) {
                labelToBlock.put(instrs.getFirst().getLabel().get(), block);
            }
        }

        // ラベルの関連付け
        for (BasicBlock<T> block : blocks) {
            var lastInstr = block.getInstructions().getLast();
            if (lastInstr.isBranch()) {
                String targetLabel = lastInstr.label;
                var targetBlock = labelToBlock.get(targetLabel);
                if (targetBlock != null) {
                    block.addSuccessor(targetBlock);
                    targetBlock.addPredecessor(block);
                }
                if (lastInstr.isConditionalBranch()) {
                    // 条件分岐命令の場合、次のブロックも後続ブロックになる
                    int currentIndex = blocks.indexOf(block);
                    if (currentIndex + 1 < blocks.size()) {
                        BasicBlock<T> nextBlock = blocks.get(currentIndex + 1);
                        block.addSuccessor(nextBlock);
                        nextBlock.addPredecessor(block);
                    }
                }
            }else if(lastInstr.isTerminal()){
                // 終端命令の場合、後続ブロックはない
            }else{
                // 最後の命令がジャンプ命令でない場合、次のブロックが後続ブロックになる
                int currentIndex = blocks.indexOf(block);
                if (currentIndex + 1 < blocks.size()) {
                    BasicBlock<T> nextBlock = blocks.get(currentIndex + 1);
                    block.addSuccessor(nextBlock);
                    nextBlock.addPredecessor(block);
                }
            }
        }
        return blocks;
    }
}
