package LeetCode;

/**
 * https://leetcode.cn/problems/search-in-a-binary-search-tree
 */
public class P700_二叉搜索树中的搜索 {

    public TreeNode searchBST(TreeNode root, int val) {

        TreeNode cur = root;
        while (cur != null){
            if(cur.val == val){
                return cur;
            }else if(cur.val < val){
                cur = cur.right;
            }else{
                cur = cur.left;
            }
        }
        return cur;
    }

    public class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode() {
        }

        TreeNode(int val) {
            this.val = val;
        }

        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }
}
