package LeetCode;

import Tree.TreeNode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class P103_二叉树的锯齿形层序遍历 {
    // 层序遍历
    public List<List<Integer>> zigzagLevelOrder(TreeNode root) {
        List<List<Integer>> ans = new ArrayList<>();
        if (root == null) return ans;
        // 根部先加入
        List<Integer> res = new ArrayList<>();
        res.add(root.val);
        ans.add(res);
        LinkedList<TreeNode> queue = new LinkedList<>();
        queue.addFirst(root);
        boolean rightToLeft = true;
        while (!queue.isEmpty()) {
            int size = queue.size();
            res = new ArrayList<>();
            // 每一层
            for (int i = 0; i < size; i++) {
                if (rightToLeft) {// 从右到左
                    TreeNode cur = queue.removeLast(); //拿到末尾节点
                    if (cur.right != null) {
                        queue.addFirst(cur.right);
                        res.add(cur.right.val);
                    }
                    if (cur.left != null) {
                        queue.addFirst(cur.left);
                        res.add(cur.left.val);
                    }
                } else {
                    // 从左到右
                    TreeNode cur = queue.removeFirst(); //拿到头部
                    // 往尾部放
                    if (cur.left != null) {
                        queue.addLast(cur.left);
                        res.add(cur.left.val);
                    }
                    if (cur.right != null) {
                        queue.addLast(cur.right);
                        res.add(cur.right.val);
                    }
                }
            }
            rightToLeft = !rightToLeft;// 转变方向
            // 避免当前层级都没有下层节点的情况
            if (!res.isEmpty()) {
                ans.add(res);
            }
        }
        return ans;
    }

}
