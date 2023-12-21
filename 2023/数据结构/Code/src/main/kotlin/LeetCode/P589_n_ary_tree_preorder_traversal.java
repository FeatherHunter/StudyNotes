package LeetCode;

import java.util.*;

/**
 * 589. N 叉树的前序遍历
 * https://leetcode.cn/problems/n-ary-tree-preorder-traversal/
 */
public class P589_n_ary_tree_preorder_traversal {

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
    public static List<Integer> preorder(Node root) {
        List<Integer> ans = new ArrayList<>();
        if(root == null) return ans;
        HashSet<Node> set = new HashSet<>();
        Deque<Node> stack = new ArrayDeque<>();
        stack.push(root);
        ans.add(root.val);
        set.add(root);
        Node cur = null;
        while (!stack.isEmpty()){
            cur = stack.pop();
            for (Node child : cur.children) {
                if(!set.contains(child)){
                    stack.push(cur);
                    stack.push(child);
                    set.add(child);
                    ans.add(child.val); //
                    break;
                }
            }
        }
        return ans;
    }
}
