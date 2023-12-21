package LeetCode;

import Tree.TreeNode;

public class P99_恢复二叉搜索树 {
    // mirrors遍历的完美应用
    // 1. 将问题转换为1,2,3,4,5被颠倒为,1,3,2,4问题，如何恢复
    // 2. 用pre记录上一个节点
    public void recoverTree(TreeNode root) {
        mirrors(root);
    }

    public void mirrors(TreeNode root) {
        TreeNode cur = root;
        TreeNode pre = null; // 上一个节点
        TreeNode x = null;
        TreeNode y = null;
        while (cur != null) {
            if (cur.left != null) {
                TreeNode mostRight = cur.left;
                while (mostRight.right != null && mostRight.right != cur) {
                    mostRight = mostRight.right;
                }
                if (mostRight.right == null) {
                    //第一次访问到
                    mostRight.right = cur;
                    cur = cur.left;
                    continue;
                } else {
                    // 第二次访问到
                    mostRight.right = null; // 恢复
                }
            }
            ////// 这里是中序
            // 情况一 cur left树都遍历结束了了，两轮了，需要cur往right走。
            // 情况二 cur.left为空，直接往右走
            if (pre != null && pre.val > cur.val) {
                if (x == null) {
                    x = pre;
                    y = cur; // 假如还能找到下一个，自动被替换
                } else {
                    y = cur; // 第二个数，应该是小的那个
                }
            }
            pre = cur;
            cur = cur.right;
        }
        /**
         * 处理变换：
         * 情况一：两个都不符合要求 xy直接交换
         * 情况二：x不符合，找不到y，那就交换，x和x+1（说明是这两个情况导致的）
         */
        if (x != null) {
            int temp = x.val;
            x.val = y.val;
            y.val = temp;
        }
    }

}
