package Graph;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class Code_01_BFS {
    public static void bfs(Node node){
        if(node == null){
            return;
        }
        Queue<Node> queue = new LinkedList<>();
        Set<Node> set = new HashSet<>();
        set.add(node);
        queue.offer(node);
        while (!queue.isEmpty()){
            Node n = queue.poll();
            System.out.println(n.val); // 打印
            for (Node next : n.nexts) {
                if(set.add(next)){
                    // 添加成功，已经有啦
                    queue.add(next);
                }
            }
        }
    }
}
