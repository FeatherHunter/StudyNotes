package LeetCode;

import Tree.TreeNode;

import java.util.HashMap;

public class P105_从前序与中序遍历序列构造二叉树 {
    public TreeNode buildTree(int[] preorder, int[] inorder) {
        /**
         * 提前做好在中序遍历中的下标映射
         */
        HashMap<Integer, Integer> inMap = new HashMap<>();
        for (int i = 0; i < inorder.length; i++) {
            inMap.put(inorder[i], i);
        }
        return buildTreeDfs(preorder, 0, 0, inorder.length - 1, inMap);
    }

    // preorder 前序遍历 [根节点，[前序左子树遍历结果]，[前序右子树遍历结果]]
    // inorder 中序遍历 [[中序左子树遍历结果]，根节点，[中序右子树遍历结果]]
    public TreeNode buildTreeDfs(int[] preorder, int root, int start, int end, HashMap<Integer, Integer> inMap) {
        if (start > end) {
            return null;
        }
        /**
         * 找到根节点在中序遍历的下标
         */
        int index = inMap.get(preorder[root]); // 找到左子树和右子树的范围划分，左子树，右子树
        TreeNode node = new TreeNode(preorder[root]);
        // 左子树的前序范围
        node.left = buildTreeDfs(preorder
                , root + 1
                , start
                , index - 1
                , inMap);
        // 右子树的前序范围
        node.right = buildTreeDfs(preorder
                , root + index - start + 1 // root 在 前序中的位置， + （index - start + 1）跳过中序中左子树的宽度，到下一个根上
                , index + 1
                , end
                , inMap);
        return node;
    }

}
