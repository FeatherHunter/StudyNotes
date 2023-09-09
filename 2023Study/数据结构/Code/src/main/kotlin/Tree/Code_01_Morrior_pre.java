package Tree;

import java.util.ArrayList;
import java.util.List;
import Tree.Code_01_Morrior.TreeNode;

public class Code_01_Morrior_pre {
    public List<Integer> preorderTraversal(TreeNode root) {
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
                    ans.add(cur.val); // 2次中第一次访问
                    cur = cur.left;
                    continue;
                }else{
                    mostRight.right = null;
                }
            }else{
                ans.add(cur.val); // 只访问一次的
            }
            cur = cur.right;
        }
        return ans;
    }
}
