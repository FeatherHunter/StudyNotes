package LeetCode;

import Tree.TreeNode;

import java.util.ArrayDeque;

public class P230_二叉搜索树中第K小的元素 {

    public int kthSmallest(TreeNode root, int k) {
        TreeNode cur = root;
        ArrayDeque<TreeNode> stack = new ArrayDeque<>();
        while (cur != null || !stack.isEmpty()){
            while (cur != null){
                stack.push(cur);
                cur = cur.left;
            }
            // 此时cur = null
            cur = stack.pop();
            k--; //弹出一个
            if(k == 0){
                return cur.val; // 打印当前节点
            }
            cur = cur.right;// 当前节点为右边节点，有右节点会继续push进去，没有就代表结束了，要往上走pop
        }
        return -1;
    }
}
