package enshud.s4.compiler.generator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LivenessAnalyzer<R extends AnalysableInstruction> {

    private final List<BasicBlock<R>> blocks;

    public LivenessAnalyzer(List<BasicBlock<R>> blocks) {
        this.blocks = blocks;
    }

    // -------------------------
    // 1. TIRBlock の liveIn / liveOut を計算
    // -------------------------
    public void computeBlockLiveness() {
        // 初期化
        for (BasicBlock<R> b : blocks) {
            b.liveIn  = new HashSet<>();
            b.liveOut = new HashSet<>();
        }

        boolean changed;

        do {
            changed = false;

            // 逆順（後方分析なので基本は後ろから）
            for (int i = blocks.size() - 1; i >= 0; i--) {
                BasicBlock<R> b = blocks.get(i);

                var oldIn  = new HashSet<>(b.liveIn);
                var oldOut = new HashSet<>(b.liveOut);

                // liveOut[B] = ∪ liveIn[succ]
                Set<AllocatableRegister> newOut = new HashSet<>();
                for (BasicBlock<R> succ : b.getSuccessors()) {
                    newOut.addAll(succ.liveIn);
                }
                b.liveOut = newOut;

                // liveIn[B] = use[B] ∪ (liveOut[B] − def[B])
                var newIn = new HashSet<>(b.getUse());
                var outMinusDef = new HashSet<>(b.liveOut);
                outMinusDef.removeAll(b.getDef());
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
        for (BasicBlock<R> block : blocks) {

            // ブロックの末尾は block.liveOut
            var live = new HashSet<>(block.liveOut);

            var insts = block.getInstructions();

            // 逆順で命令を処理
            for (int i = insts.size() - 1; i >= 0; i--) {
                var inst = insts.get(i);

                inst.liveOut = new HashSet<>(live);

                // liveIn = use(inst) ∪ (liveOut − def(inst))
                var in = new HashSet<>(inst.liveOut);
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
//        for (BasicBlock<R> block : blocks) {
//            System.out.println("Block:");
//            for (AnalysableInstruction instr : block.getInstructions()) {
//                System.out.println("  " + instr);
//            }
//            System.out.println("  use: " + block.getUse());
//            System.out.println("  def: " + block.getDef());
//            System.out.println("  liveIn: " + block.liveIn);
//            System.out.println("  liveOut: " + block.liveOut);
//        }
        computeInstrLiveness();
        // show instruction liveness for debug
//        for (BasicBlock<R> block : blocks) {
//            for (AnalysableInstruction instr : block.getInstructions()) {
//                System.out.println(instr);
//                System.out.println("  liveIn: " + instr.liveIn);
//                System.out.println("  liveOut: " + instr.liveOut);
//            }
//        }
    }
}
