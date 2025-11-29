package enshud.s4.compiler.generator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LivenessAnalyzer<T, R extends AnalysableInstruction<T>> {

    private final List<BasicBlock<T, R>> blocks;

    public LivenessAnalyzer(List<BasicBlock<T, R>> blocks) {
        this.blocks = blocks;
    }

    // -------------------------
    // 1. TIRBlock の liveIn / liveOut を計算
    // -------------------------
    public void computeBlockLiveness() {
        // 初期化
        for (BasicBlock<T, R> b : blocks) {
            b.liveIn  = new HashSet<>();
            b.liveOut = new HashSet<>();
        }

        boolean changed;

        do {
            changed = false;

            // 逆順（後方分析なので基本は後ろから）
            for (int i = blocks.size() - 1; i >= 0; i--) {
                BasicBlock<T, R> b = blocks.get(i);

                Set<T> oldIn  = new HashSet<>(b.liveIn);
                Set<T> oldOut = new HashSet<>(b.liveOut);

                // liveOut[B] = ∪ liveIn[succ]
                Set<T> newOut = new HashSet<>();
                for (BasicBlock<T, R> succ : b.getPredecessors()) {
                    newOut.addAll(succ.liveIn);
                }
                b.liveOut = newOut;

                // liveIn[B] = use[B] ∪ (liveOut[B] − def[B])
                Set<T> newIn = new HashSet<>(b.getDef());
                Set<T> outMinusDef = new HashSet<>(b.liveOut);
                outMinusDef.removeAll(b.getUse());
                newIn.addAll(outMinusDef);
                b.liveIn = newIn;

                if (!b.liveIn.equals(oldIn) || !b.liveOut.equals(oldOut)) {
                    changed = true;
                }
            }

        } while (changed);
    }

    // -------------------------
    // 2. 命令単位の LiveIn / LiveOut を計算
    // -------------------------
    public void computeInstrLiveness() {
        for (BasicBlock block : blocks) {

            // ブロックの末尾は block.liveOut
            Set<T> live = new HashSet<>(block.liveOut);

            List<AnalysableInstruction<T>> insts = block.getInstructions();

            // 逆順で命令を処理
            for (int i = insts.size() - 1; i >= 0; i--) {
                AnalysableInstruction<T> inst = insts.get(i);

                inst.liveOut = new HashSet<>(live);

                // liveIn = use(inst) ∪ (liveOut − def(inst))
                Set<T> in = new HashSet<>(inst.liveOut);
                in.removeAll(inst.getDef());
                in.addAll(inst.getUse());

                inst.liveIn = in;
                live = in;
            }
        }
    }

    // -------------------------
    // 3. 全体の Liveness を計算する
    // -------------------------
    public void analyze() {
        computeBlockLiveness();
        // show block liveness for debug
        for (BasicBlock<T, R> block : blocks) {
            System.out.println("Block:");
            for (AnalysableInstruction<T> instr : block.getInstructions()) {
                System.out.println("  " + instr);
            }
            System.out.println("  liveIn: " + block.liveIn);
            System.out.println("  liveOut: " + block.liveOut);
        }
        computeInstrLiveness();
        // show instruction liveness for debug
//        for (BasicBlock<T, R> block : blocks) {
//            for (AnalysableInstruction<T> instr : block.getInstructions()) {
//                System.out.println(instr);
//                System.out.println("  liveIn: " + instr.liveIn);
//                System.out.println("  liveOut: " + instr.liveOut);
//            }
//        }
    }
}
