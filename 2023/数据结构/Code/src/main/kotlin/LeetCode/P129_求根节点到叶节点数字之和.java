package LeetCode;

import Tree.TreeNode;

import java.util.ArrayList;
import java.util.List;

public class P129_求根节点到叶节点数字之和 {
    public int sumNumbers(TreeNode root) {
        List<String> res = dfs(root);
        int ans = 0;
        for (String num : res) {
            ans += Integer.valueOf(num);
        }
        return ans;
    }

    public List<String> dfs(TreeNode node) {
        List<String> res = new ArrayList<>();
        if (node.left != null) {
            List<String> left = dfs(node.left);
            for (String num : left) {
                res.add(node.val + num);
            }
        }
        if (node.right != null) {
            List<String> right = dfs(node.right);
            for (String num : right) {
                res.add(node.val + num);
            }
        }
        // 当前为叶子节点
        if (node.left == null && node.right == null) {
            res.add(node.val + "");
        }
        return res;
    }

}
