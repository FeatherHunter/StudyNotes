package Graph;

import DisjointSetUnion.UnionSet;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class Code_05_Prim {

    public static Set<Edge> prim(Graph graph){
        PriorityQueue<Edge> heap = new PriorityQueue<Edge>((a,b)->{
            return a.weight - b.weight;
        });

        HashSet<Node> set = new HashSet();

        Set<Edge> reuslt = new HashSet();
        for (Node node : graph.nodes.values()) {
            set.add(node);  // 随便一个点开始
            for (Edge edge : node.edges) {
                heap.offer(edge); // 解锁该边
            }
            while (!heap.isEmpty()){
                Edge edge = heap.poll();
                if(set.add(edge.to)){ // to可以加进去
                    reuslt.add(edge);
                    // 能加入说明，不是回路
                    for (Edge e : edge.to.edges) {
                        heap.offer(e); // 添加到下一个
                    }
                }else{
                    // 是回路，不能继续处理哦。找下一个边边
                }
                if(set.size() == graph.nodes.size()){
                    return reuslt; // 提前结束哦
                }
            }

            break;
        }
        if(set.size() < graph.nodes.size()){
            return null; // 没办法找到所有点。
        }

        return reuslt;
    }
}
