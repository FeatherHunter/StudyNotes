package LeetCode;

import Tree.TreeNode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class P95_不同的二叉搜索树II {
    /**
     * 深度遍历递归
     */
    public List<TreeNode> generateTrees(int n) {
        if (n < 1) {
            return new ArrayList<>();
        }
        return dfs(1, n);
    }

    // 从start到end构造出所有二叉搜索树，返回树头集合
    public List<TreeNode> dfs(int start, int end) {
        List<TreeNode> alltrees = new LinkedList<>();
        if (start > end) {
            alltrees.add(null);
            return alltrees;
        }
        for (int i = start; i <= end; i++) {
            List<TreeNode> left = dfs(start, i - 1);
            List<TreeNode> right = dfs(i + 1, end);
            // 遍历子树可能列表
            for (TreeNode lnode : left) {
                for (TreeNode rnode : right) {
                    TreeNode root = new TreeNode(i); // 根部节点
                    root.left = lnode;
                    root.right = rnode;
                    alltrees.add(root);
                }
            }
        }
        return alltrees;
    }

}
