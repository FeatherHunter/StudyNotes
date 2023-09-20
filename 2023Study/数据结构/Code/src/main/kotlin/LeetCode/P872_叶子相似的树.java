package LeetCode;

import Tree.Code_01_Morrior;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class P872_叶子相似的树 {

    public boolean leafSimilar(TreeNode root1, TreeNode root2) {

        List<Integer> ans1 = leefTraversal(root1);
        List<Integer> ans2 = leefTraversal(root2);
        if(ans1.size() != ans2.size()){
            return false;
        }
        for (int i = 0; i < ans1.size(); i++) {
            if(ans1.get(i) != ans2.get(i)) return false;
        }
        return true;
    }

    public List<Integer> leefTraversal(TreeNode root) {
        List<Integer> ans = new ArrayList<>();
        TreeNode cur = root;
        TreeNode mostRight = null;
        while (cur != null){
            mostRight = cur.left;
            if(mostRight != null){
                while (mostRight.right != null && mostRight.right != cur){
                    mostRight = mostRight.right;
                }
                if(mostRight.right == null){
                    mostRight.right = cur; // first
                    cur = cur.left;
                    continue;
                }else{
                    mostRight.right = null;
                    if(mostRight.left == null){
                        ans.add(mostRight.val); // left = null，right = null的叶子结点
                    }
                }
            }
            if(cur.right == null && cur.left == null){
                ans.add(cur.val); // root节点最右边的叶子结点
            }
            cur = cur.right;
        }
        return ans;
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
