package LeetCode;

public class P938_二叉搜索树的范围和 {
    public int rangeSumBST(P501_二叉搜索树中的众数.TreeNode root, int low, int high) {
        Info res = dfs(root, low, high);
        return res.sum;
    }

    public Info dfs(P501_二叉搜索树中的众数.TreeNode node, int low, int high){
        if(node == null){
            return new Info(0);
        }
        Info left = dfs(node.left, low, high);
        Info right = dfs(node.right, low, high);
        return new Info(left.sum + right.sum + ((node.val >= low && node.val <= high) ? node.val : 0));
    }

    class Info{
        int sum;

        public Info(int sum) {
            this.sum = sum;
        }
    }
}
