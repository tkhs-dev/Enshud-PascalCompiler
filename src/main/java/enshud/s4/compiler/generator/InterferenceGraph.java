package enshud.s4.compiler.generator;

import enshud.s4.compiler.generator.allocate.TargetDescription;

import java.util.*;
import java.util.function.Function;

public class InterferenceGraph {

    // 元の完全な干渉関係
    private final Map<AllocatableRegister, Set<AllocatableRegister>> originalAdj = new HashMap<>();

    // 現在の干渉グラフ（Simplifyでノードが削除される）
    private final Map<AllocatableRegister, Set<AllocatableRegister>> currentAdj = new HashMap<>();

    public void addNode(AllocatableRegister r) {
        originalAdj.putIfAbsent(r, new HashSet<>());
        currentAdj.putIfAbsent(r, new HashSet<>());
    }

    public void addEdge(AllocatableRegister a, AllocatableRegister b) {
        if (a.equals(b)) return;

        originalAdj.get(a).add(b);
        originalAdj.get(b).add(a);

        currentAdj.get(a).add(b);
        currentAdj.get(b).add(a);
    }

    //---------------------------------------------------------
    // Simplify フェーズ: ノードを削除する（currentAdj から消す）
    //---------------------------------------------------------
    public void removeNode(AllocatableRegister r) {
        // currentAdj の情報だけ操作する
        Set<AllocatableRegister> neighs = currentAdj.get(r);
        if (neighs == null) return;

        for (AllocatableRegister n : neighs) {
            currentAdj.get(n).remove(r);
        }
        currentAdj.get(r).clear();
    }

    //---------------------------------------------------------
    // Select フェーズ: ノードを復元する
    //---------------------------------------------------------
    public void restoreNode(AllocatableRegister r) {
        // originalAdj の内容から currentAdj を復元
        Set<AllocatableRegister> orig = originalAdj.get(r);

        for (AllocatableRegister n : orig) {
            // ただし、相手ノードが "すでに削除済み" なら復元しない
            if (currentAdj.containsKey(n)) {
                currentAdj.get(r).add(n);
                currentAdj.get(n).add(r);
            }
        }
    }

    //---------------------------------------------------------
    // 現在のグラフ（Simplify 途中）の隣接取得
    //---------------------------------------------------------
    public Set<AllocatableRegister> getAdjacents(AllocatableRegister r) {
        return currentAdj.getOrDefault(r, Set.of());
    }

    public Set<AllocatableRegister> getNodes() {
        return currentAdj.keySet();
    }

    public static InterferenceGraph build(List<? extends BasicBlock<? extends AnalysableInstruction>> blocks, TargetDescription desc, Function<Register, AllocatableRegister> mapper) {
        var graph = new InterferenceGraph();

        for (var block : blocks) {
            for (var inst : block.getInstructions()) {

                // def された変数（多くても1つと仮定）
                var defs = inst.getDef();

                for (var d : defs) {
                    graph.addNode(d);

                    // def(d) は liveOut のすべてと干渉する
                    for (var live : inst.liveOut) {
                        graph.addEdge(d, live);
                    }

                    // CALL 命令の場合：caller-save レジスタも追加
                    if (inst.isCall()) {
                        var callerSaved = desc.getCallerSaved().stream().map(mapper).toList();

                        for (var csr : callerSaved) {
                            graph.addNode(csr);
                            graph.addEdge(d, csr);
                            for (var live : inst.liveOut) {
                                graph.addEdge(csr, live);
                            }
                        }
                    }
                }

                // use だけの命令でも、干渉グラフへノード追加
                for (var u : inst.getUse()) {
                    graph.addNode(u);
                }
            }
        }
        System.out.println(graph.originalAdj);
        System.out.println(graph.currentAdj);

        return graph;
    }
}
