package Tree;

public class Code_01_Morrior {
    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode() {}
        TreeNode(int val) {
            this.val = val;
        }
        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }

    /**
     * 1. 当前节点的左节点（cur.left）的右子树最右侧节点(cur.left.right.right...)
     *  1. 为空：right = cur（第一次访问）
     *  2. 为cur：right = null（第二次访问）
     * 2. 遍历左子树(cur.left.left...)直至为空，cur = cur.right
     */
    public static void morrior(TreeNode root) {
        if (root == null) return;
        TreeNode cur = root;
        TreeNode mostRight = null;
        while (cur != null){
            mostRight = cur.left;
            if(cur.left != null){
                while (mostRight.right != null || mostRight.right != cur){
                    mostRight = mostRight.right;
                }
                if(mostRight.right == null){
                    mostRight.right = cur; // first
                    cur = cur.left;
                    continue; // 说明第一次访问，再次循环，不能让cur到right
                }else{
                    mostRight.right = null;
                }
            }
            cur = cur.right;
        }
    }
}
