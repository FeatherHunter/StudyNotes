package Graph;

import java.util.*;

public class Code_03_TopologySort {
    public static List<Node> topological(Graph graph){
        HashMap<Node, Integer> inMap = new HashMap<Node, Integer>();
        Queue<Node> zeroQueue = new LinkedList<>();
        for (Node node : graph.nodes.values()) {
            inMap.put(node, node.in);
            if(node.in == 0){
                zeroQueue.offer(node);
            }
        }

        List<Node> result = new ArrayList<Node>();
        while(!zeroQueue.isEmpty()){
            Node node = zeroQueue.poll();
            System.out.println(node.val); // 打印
            result.add(node);
            for (Node next : node.nexts) {
                int in = inMap.get(next);
                in--;
                inMap.put(next, in);
                if(in == 0){
                    zeroQueue.offer(next); // 加入0队列
                }
            }
        }
        return result;
    }
}
