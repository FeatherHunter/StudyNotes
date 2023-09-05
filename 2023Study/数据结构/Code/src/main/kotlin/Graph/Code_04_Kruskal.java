package Graph;

import DisjointSetUnion.UnionSet;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class Code_04_Kruskal {
    public static Set<Edge> kruskal(Graph graph){
        // 得到最小生成树的权值
        PriorityQueue<Edge> heap = new PriorityQueue<Edge>((a,b)->{
            return a.weight - b.weight;
        }); // 存放最小权值的边。
        for (Edge edge : graph.edges) {
            heap.offer(edge);
            // 填充入所有边
        }

        UnionSet<Node> unionSet = new UnionSet<Node>();
        for (Node node : graph.nodes.values()) {
            unionSet.insert(node); // 填充入所有点
        }

        Set<Edge> result = new HashSet<Edge>();

        while(!heap.isEmpty()){
            Edge edge = heap.poll();
            if(unionSet.union(edge.from, edge.to)){
                // 合并成功
                result.add(edge);
                if(unionSet.size() == 1){
                    return result;
                }
            }// else 合并失败

        }

        return result;
    }
}
