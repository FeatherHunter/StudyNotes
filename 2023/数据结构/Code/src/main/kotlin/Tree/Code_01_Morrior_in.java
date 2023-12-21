package Tree;

import Tree.Code_01_Morrior.TreeNode;

import java.util.ArrayList;
import java.util.List;

public class Code_01_Morrior_in {
    public List<Integer> inorderTraversal(TreeNode root) {
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
                    ans.add(cur.val); // 2次中第2次访问
                }
            }else{
                ans.add(cur.val); // 只访问一次的
            }
            cur = cur.right;
        }
        return ans;
    }
}
