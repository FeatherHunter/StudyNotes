package LeetCode;

import Tree.TreeNode;

import java.util.ArrayList;
import java.util.List;

public class P113_路径总和_II {
    // 二叉树套路
    public List<List<Integer>> pathSum(TreeNode root, int targetSum) {
        if (root == null) return new ArrayList<>(); // 为空
        PathInfo info = process(root, targetSum);
        return info.res;
    }

    public PathInfo process(TreeNode node, int targetSum) {
        if (node.left == null && node.right == null) {
            List<List<Integer>> res = new ArrayList<>();
            List<Integer> ans = new ArrayList<>();
            if (node.val == targetSum) {
                System.out.println(node.val);
                ans.add(node.val);      // 有路径结果
                res.add(ans);
            }
            return new PathInfo(res);
        }
        List<List<Integer>> res = null;
        if (node.left != null) {
            PathInfo left = process(node.left, targetSum - node.val);
            res = left.res;
        }
        if (node.right != null) {
            PathInfo right = process(node.right, targetSum - node.val);
            if (res == null) {
                res = right.res;
            } else {
                res.addAll(right.res);
            }
        }
        for (List<Integer> list : res) {
            list.add(0, node.val);
        }
        return new PathInfo(res); // 存放了结果，也可能为空
    }

    class PathInfo {
        List<List<Integer>> res;

        public PathInfo(List<List<Integer>> res) {
            this.res = res;
        }
    }

}
