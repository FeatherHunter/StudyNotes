package Graph;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

public class Code_02_DFS {
    public static void dfs(Node node){
        if(node == null){
            return;
        }
        Set<Node> set = new HashSet<>();
        Deque<Node> stack = new ArrayDeque<Node>();
        set.add(node);
        stack.push(node);
        System.out.println(node.val);
        while (!stack.isEmpty()){
            Node n = stack.pop();
            for (Node next : n.nexts) {
                if(set.add(next)){
                    // 没有扫描过
                    stack.push(n);
                    stack.push(next);
                    System.out.println(next.val);
                    break;
                }
            }
        }
    }
}
