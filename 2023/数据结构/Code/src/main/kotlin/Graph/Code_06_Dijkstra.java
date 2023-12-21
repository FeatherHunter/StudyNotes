package Graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;

public class Code_06_Dijkstra {
    public static HashMap<Node, Integer> dijkstra(Node start){
        HashMap<Node, Integer> distanceMap = new HashMap<Node, Integer>(); // 距离表
        distanceMap.put(start, 0);
        HashSet<Node> selectedNodes = new HashSet<Node>();
        Node minNode = getMinDistanceAndUnselectedNode(distanceMap, selectedNodes); // 获取到距离表中最小，且没有检查过的点
        while(minNode != null){
            int distance = distanceMap.get(minNode);
            for (Edge edge : minNode.edges) {
                Node toNode = edge.to;
                if(distanceMap.containsKey(toNode)){
                    if(distance + edge.weight < distanceMap.get(toNode)){
                        distanceMap.put(toNode, distance + edge.weight); // 包含节点，需要比较
                    }
                }else{
                    distanceMap.put(toNode, distance + edge.weight); // 没有该点，直接加入
                }
            }
            selectedNodes.add(minNode);
            minNode = getMinDistanceAndUnselectedNode(distanceMap, selectedNodes);
        }
        return distanceMap;
    }

    public static Node getMinDistanceAndUnselectedNode(HashMap<Node, Integer> distanceMap, HashSet<Node> selectedNodes){
        Node minNode = null;
        int min = Integer.MAX_VALUE;
        for (Map.Entry<Node, Integer> entry : distanceMap.entrySet()) {
            if(!selectedNodes.contains(entry.getKey())){
                if(entry.getValue() < min){
                    min = entry.getValue();
                    minNode = entry.getKey();
                }
            }
        }
        return minNode;
    }
}
