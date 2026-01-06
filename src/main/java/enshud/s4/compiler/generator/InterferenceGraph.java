package enshud.s4.compiler.generator;

import enshud.s4.compiler.generator.allocate.TargetDescription;

import java.util.*;

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
    public void removeNode(AllocatableRegister node) {
        // 1. ノード自身のエントリを削除する
        Set<AllocatableRegister> neighbors = currentAdj.remove(node);
        if (neighbors == null) return;

        // 2. 他のノードの隣接リストから node を削除する
        for (AllocatableRegister adj : neighbors) {
            Set<AllocatableRegister> set = currentAdj.get(adj);
            if (set != null) {
                set.remove(node);
            }
        }
    }

    //---------------------------------------------------------
    // Select フェーズ: ノードを復元する
    //---------------------------------------------------------
    public void restoreNode(AllocatableRegister r) {
        // originalAdj から復元すべき隣接ノード集合を取得
        Set<AllocatableRegister> orig = originalAdj.get(r);
        if (orig == null) {
            // originalAdj になければ干渉関係を持たないノード
            currentAdj.putIfAbsent(r, new HashSet<>());
            return;
        }

        // 1. 自ノード r のエントリを currentAdj に作成する（ない場合）
        currentAdj.putIfAbsent(r, new HashSet<>());

        // 2. 隣接ノードを復元する
        for (AllocatableRegister n : orig) {
            // 相手がまだグラフに残っていないなら（Simplify 済みなら）復元しない
            if (!currentAdj.containsKey(n)) {
                continue;
            }

            // 自ノード側に追加
            currentAdj.get(r).add(n);

            // 相手側にも r を追加（相互干渉）
            currentAdj.get(n).add(r);
        }
    }

    public void restoreAll() {
        currentAdj.clear();
        for (var entry : originalAdj.entrySet()) {
            currentAdj.put(entry.getKey(), new HashSet<>(entry.getValue()));
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

    public static InterferenceGraph build(List<? extends BasicBlock<? extends AnalysableInstruction>> blocks, TargetDescription desc) {
        var graph = new InterferenceGraph();

        // 最初のブロックで　liveIn されている変数を擬似的にdef しているとみなす
        var entryBlock = blocks.getFirst();
        for (var r : entryBlock.liveIn) {
            graph.addNode(r);
        }

        for (var block : blocks) {
            for (var inst : block.getInstructions()) {

                // def された変数
                var defs = inst.getDef();

                defs.forEach(graph::addNode);

                for (var d : defs) {
                    // def(d) は liveOut のすべてと干渉する
                    for (var live : inst.liveOut) {
                        graph.addEdge(d, live);
                    }
                }

                // use だけの命令でも、干渉グラフへノード追加
                for (var u : inst.getUse()) {
                    graph.addNode(u);
                }
            }
        }

        return graph;
    }
}
