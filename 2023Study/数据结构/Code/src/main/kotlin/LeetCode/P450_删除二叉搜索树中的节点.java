package LeetCode;

import Tree.TreeNode;

public class P450_删除二叉搜索树中的节点 {
    /**
     * 0ms 击败 100.00%使用 Java 的用户
     */
    public TreeNode deleteNode(TreeNode root, int key) {
        // 1、找到节点
        TreeNode cur = root;

        TreeNode flag = new TreeNode();
        flag.left = root;
        TreeNode pre = flag;
        while (cur != null) {
            if (key > cur.val) {
                pre = cur;
                cur = cur.right;
            } else if (key < cur.val) {
                pre = cur;
                cur = cur.left;
            } else {
                break;
            }
        }
        if (cur == null) return root; // 没找到
        // 2、删除 // 自己是叶子，直接删除 // 自己只有左孩子，自己只有右孩子，替代自己就行了
        // 自由有left、right。用right的left
        if (cur.left == null || cur.right == null) {
            if (cur == pre.left) {
                pre.left = (cur.left != null) ? cur.left : cur.right;
            } else {
                pre.right = (cur.left != null) ? cur.left : cur.right;
            }
        } else {
            // left right都存在
            if (cur.right.left == null) {
                cur.right.left = cur.left;
                if (cur == pre.left) {
                    pre.left = cur.right;
                } else {
                    pre.right = cur.right;
                }
            } else {
                TreeNode mostLeft = cur.right;
                while (mostLeft.left.left != null) {
                    mostLeft = mostLeft.left;
                }
                cur.val = mostLeft.left.val; // 交换值
                // 删除目标节点
                mostLeft.left = mostLeft.left.right;
            }
        }
        return flag.left; // 返回哨兵的left（默认头，可能是null，也可能有）
    }

}
