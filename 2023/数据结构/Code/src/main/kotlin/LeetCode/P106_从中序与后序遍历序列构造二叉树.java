package LeetCode;

import Tree.TreeNode;

import java.util.HashMap;

public class P106_从中序与后序遍历序列构造二叉树 {
    // inorder 中序遍历 [[中序左子树遍历结果]，根节点，[中序右子树遍历结果]]
    // postorder后序遍历 [[后序左子树遍历结果]，[后序右子树遍历结果]，根节点]
    public TreeNode buildTree(int[] inorder, int[] postorder) {
        HashMap<Integer, Integer> inMap = new HashMap<>();
        for (int i = 0; i < inorder.length; i++) {
            inMap.put(inorder[i], i);
        }
        return buildTreePostDfs(postorder, postorder.length - 1, 0, inorder.length - 1, inMap);
    }

    public TreeNode buildTreePostDfs(int[] postorder, int root, int left, int right, HashMap<Integer, Integer> inMap) {
        if (left > right) return null;
        TreeNode node = new TreeNode(postorder[root]);
        int index = inMap.get(postorder[root]);
        // 后续，根节点，是root
        node.left = buildTreePostDfs(postorder, root - (right - index) - 1, left, index - 1, inMap);
        node.right = buildTreePostDfs(postorder, root - 1, index + 1, right, inMap);
        return node; //返回根部节点
    }

}
