package enshud.s4.compiler.generator;

import enshud.s4.compiler.instruction.BasicBlock;
import enshud.s4.compiler.instruction.FunctionUnit;
import enshud.s4.compiler.instruction.Instruction;
import enshud.s4.compiler.instruction.UseDef;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LivenessAnalyzer<R, T extends Instruction & UseDef<R>> {

    private final List<BasicBlock<T>> blocks;

    private Map<BasicBlock<T>, Set<R>> blockLiveIn;
    private Map<BasicBlock<T>, Set<R>> blockLiveOut;

    private Map<T, Set<R>> instrLiveIn;
    private Map<T, Set<R>> instrLiveOut;

    public LivenessAnalyzer(FunctionUnit<T> function) {
        this.blocks = function.getBlocks();

        this.blockLiveIn  = new java.util.HashMap<>();
        this.blockLiveOut = new java.util.HashMap<>();
        this.instrLiveIn  = new java.util.HashMap<>();
        this.instrLiveOut = new java.util.HashMap<>();
    }

    // -------------------------
    // 1. TIRBlock の liveIn / liveOut を計算
    // -------------------------
    private void computeBlockLiveness() {
        // 初期化
        for (BasicBlock<T> b : blocks) {
            blockLiveIn.put(b, new HashSet<>());
            blockLiveOut.put(b, new HashSet<>());
        }

        // ブロックの use / def を計算
        Map<BasicBlock<T>, Set<R>> blockUse = new java.util.HashMap<>();
        Map<BasicBlock<T>, Set<R>> blockDef = new java.util.HashMap<>();
        for (BasicBlock<T> b : blocks) {
            Set<R> useSet = new HashSet<>();
            Set<R> defSet = new HashSet<>();
            for (T instr : b.getInstructions()) {
                // def に含まれないものを use に追加
                for (R reg : instr.getUse()) {
                    if (!defSet.contains(reg)) {
                        useSet.add(reg);
                    }
                }
                // def に追加
                defSet.addAll(instr.getDef());
            }
            blockUse.put(b, useSet);
            blockDef.put(b, defSet);
        }

        boolean changed;

        do {
            changed = false;

            // 逆順（後方分析なので基本は後ろから）
            for (int i = blocks.size() - 1; i >= 0; i--) {
                BasicBlock<T> b = blocks.get(i);

                var oldIn  = blockLiveIn.get(b);
                var oldOut = blockLiveOut.get(b);

                // liveOut[B] = ∪ liveIn[succ]
                Set<R> newOut = new HashSet<>();
                for (BasicBlock<T> succ : b.getSuccessors()) {
                    newOut.addAll(blockLiveIn.get(succ));
                }
                blockLiveOut.put(b, newOut);

                // liveIn[B] = use[B] ∪ (liveOut[B] − def[B])
                var newIn = new HashSet<>(blockUse.get(b));
                var outMinusDef = new HashSet<>(blockLiveOut.get(b));
                outMinusDef.removeAll(blockDef.get(b));
                newIn.addAll(outMinusDef);
                blockLiveIn.put(b, newIn);

                if (!newIn.equals(oldIn) || !newOut.equals(oldOut)) {
                    changed = true;
                }
            }

        } while (changed);
    }

    // -------------------------
    // 2. 命令単位の LiveIn / LiveOut を計算
    // -------------------------
    private void computeInstrLiveness() {
        for (BasicBlock<T> block : blocks) {

            // ブロックの末尾は block.liveOut
            var live = new HashSet<>(blockLiveOut.get(block));

            var insts = block.getInstructions();

            // 逆順で命令を処理
            for (int i = insts.size() - 1; i >= 0; i--) {
                var inst = insts.get(i);

                instrLiveOut.put(inst, new HashSet<>(live));

                // liveIn = use(inst) ∪ (liveOut − def(inst))
                var in = new HashSet<>(live);
                in.removeAll(inst.getDef());
                in.addAll(inst.getUse());

                instrLiveIn.put(inst, in);
                live = in;
            }
        }
    }

    // -------------------------
    // 3. 全体の Liveness を計算する
    // -------------------------
    public Liveness<R, T> analyze() {
        computeBlockLiveness();
        // show block liveness for debug
//        for (BasicBlock<T> block : blocks) {
//            System.out.println("Block:");
//            for (AnalysableInstruction instr : block.getInstructions()) {
//                System.out.println("  " + instr);
//            }
//            System.out.println("  use: " + block.getUse());
//            System.out.println("  def: " + block.getDef());
//            System.out.println("  liveIn: " + blockLiveIn.get(block));
//            System.out.println("  liveOut: " + blockLiveOut.get(block));
//        }
        computeInstrLiveness();
        // show instruction liveness for debug
//        for (BasicBlock<T> block : blocks) {
//            for (AnalysableInstruction instr : block.getInstructions()) {
//                System.out.println(instr);
//                System.out.println("  liveIn: " + instrLiveIn.get(instr));
//                System.out.println("  liveOut: " + instrLiveOut.get(instr));
//            }
//        }
        return new Liveness<>(instrLiveIn, instrLiveOut, blockLiveIn, blockLiveOut);
    }
}
