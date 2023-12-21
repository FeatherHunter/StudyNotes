package LeetCode;

public class P133_克隆图 {
    public Node cloneGraph(Node node) {
        return dfsCloneGraph(node, new HashMap<>());
    }

    public Node dfsCloneGraph(Node node, HashMap<Node, Node> map) {
        if (node == null) return null; // 考虑空节点
        if (map.containsKey(node)) {
            return map.get(node);
        }
        Node copy = new Node(node.val);
        map.put(node, copy); // 存入
        for (Node neighbor : node.neighbors) {
            copy.neighbors.add(dfsCloneGraph(neighbor, map));
        }
        return copy;
    }

}
