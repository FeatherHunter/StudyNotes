package LeetCode;

import Tree.TreeNode;

import java.util.LinkedList;

public class P222_完全二叉树的节点个数 {
    public int countNodes(TreeNode root) {
        if (root == null) return 0;
        LinkedList<TreeNode> queue = new LinkedList<>();
        int size = 0;
        queue.offer(root);
        while (!queue.isEmpty()){
            int n = queue.size();
            size += n;
            for (int i = 0; i < n; i++) {
                TreeNode cur = queue.poll(); //offer+poll
                if(cur.left != null){
                    queue.offer(cur.left);
                }
                if(cur.right != null){
                    queue.offer(cur.right);
                }
            }
        }
        return size;
    }
}
