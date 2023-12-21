package LeetCode;

import Tree.TreeNode;

import java.util.HashMap;

public class P437_路径总和III {

    int count = 0;

    public int pathSum(TreeNode root, int targetSum) {
        if (root == null) return 0;

        HashMap<Long, Integer> map = new HashMap();
        map.put(0L, 1); //假设根节点就符合要求，root.val - target = 0, 需要统计出这一次
        dfs(root, map, root.val, targetSum);

        return count;
    }


    public void dfs(TreeNode cur, HashMap<Long, Integer> map, long sum, int targetSum){
        if(map.containsKey(sum - targetSum)) count+= map.get(sum - targetSum);
        map.put(sum, map.getOrDefault(sum, 0) + 1);

        if(cur.left != null) dfs(cur.left, map, sum + cur.left.val, targetSum);
        if(cur.right != null) dfs(cur.right, map, sum + cur.right.val, targetSum);

        map.put(sum, map.getOrDefault(sum, 0) - 1); // 避免处理兄弟树的时候，把这个方向的反复统计了
    }

}
