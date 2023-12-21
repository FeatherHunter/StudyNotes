package LeetCode;

import Tree.TreeNode;

/**
 * https://leetcode.cn/problems/count-good-nodes-in-binary-tree
 * 2ms 击败 100.00%使用 Java 的用户
 */
public class P1448_统计二叉树中好节点的数目 {
    public int goodNodes(TreeNode root) {
        // 以INT最小值，开始dfs
        return dfs(root, Integer.MIN_VALUE);
    }

    public int dfs(TreeNode cur, int pathMax){
        int res = 0;
        if(cur != null){
            pathMax = Math.max(pathMax, cur.val);
            // 左边数量 + 右边数量 + 根据判断当前节点是否符合要求 + 1 或者 0
            res += dfs(cur.left, pathMax) + dfs(cur.right, pathMax) + ((pathMax == cur.val)?1:0);
        }
        return res;
    }
}
