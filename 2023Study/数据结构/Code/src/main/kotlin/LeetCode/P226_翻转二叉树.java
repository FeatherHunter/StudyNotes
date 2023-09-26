package LeetCode;

import Tree.TreeNode;

public class P226_翻转二叉树 {
    public TreeNode invertTree(TreeNode root) {
        process(root);
        return root;
    }

    public void process(TreeNode node){ // 递归反转
        if(node == null) return;
        TreeNode temp = node.left;
        node.left = node.right;
        node.right = temp;
        process(node.left);
        process(node.right);
    }
}
