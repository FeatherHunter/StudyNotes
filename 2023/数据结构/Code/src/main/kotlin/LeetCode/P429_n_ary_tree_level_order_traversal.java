package LeetCode;

import com.sun.jmx.remote.internal.ArrayQueue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * 429. N 叉树的层序遍历
 * https://leetcode.cn/problems/n-ary-tree-level-order-traversal/
 * 3ms
 * 击败 85.25%使用 Java 的用户
 */
public class P429_n_ary_tree_level_order_traversal {
    static class Node {
        public int val;
        public List<Node> children;

        public Node() {}

        public Node(int _val) {
            val = _val;
        }

        public Node(int _val, List<Node> _children) {
            val = _val;
            children = _children;
        }
    };
    public static List<List<Integer>> levelOrder(Node root) {
        List<List<Integer>> ans = new ArrayList<>();
        if (root == null) return ans;
        Queue<Node> queue = new LinkedList<Node>();
        queue.offer(root);
        while (!queue.isEmpty()){
            int size = queue.size();
            ArrayList<Integer> temp = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                Node node = queue.poll();
                temp.add(node.val);
                for (Node child : node.children) {
                    queue.offer(child);
                }
            }
            ans.add(temp); // 收集一层
        }
        return ans;
    }
}
