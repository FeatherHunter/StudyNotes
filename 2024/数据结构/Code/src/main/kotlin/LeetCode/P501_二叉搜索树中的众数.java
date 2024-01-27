package LeetCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class P501_二叉搜索树中的众数 {

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

    public int[] findMode(TreeNode root) {
        List<Integer> nums = new ArrayList<>();
        HashMap<Integer, Integer> map = new HashMap<>();

        LinkedList<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        int max = 0;
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                if (!map.containsKey(node.val)) {
                    nums.add(node.val);
                    map.put(node.val, 0);
                }
                int count = map.get(node.val) + 1;
                max = Math.max(max, count);
                map.put(node.val, count);

                if (node.left != null) {
                    queue.offer(node.left);
                }
                if (node.right != null) {
                    queue.offer(node.right);
                }
            }
        }
        List<Integer> ans = new ArrayList<>();
        for (Integer num : nums) {
            if(map.get(num) == max){
                ans.add(num);
            }
        }
        int[] res = new int[ans.size()];
        for (int i = 0; i < ans.size(); i++) {
            res[i] = ans.get(i);
        }
        return res;
    }
}
