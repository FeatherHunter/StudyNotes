package LeetCode;

import Tree.TreeNode;

public class P114_二叉树展开为链表 {
    public void flatten(TreeNode root) {
        process(root, null);
    }

    public void process(TreeNode node, TreeNode parentRight) {
        if (node == null) return;
        // 需要考虑到若右节点为null，需要把右兄弟作为左孩子的parentRight
        process(node.left, node.right == null ? parentRight : node.right);
        process(node.right, parentRight);
        if (node.left != null) {
            node.right = node.left;
        } else if (node.right != null) {
            node.right = node.right;
        } else {
            node.right = parentRight;
        }
        node.left = null;
    }

}
