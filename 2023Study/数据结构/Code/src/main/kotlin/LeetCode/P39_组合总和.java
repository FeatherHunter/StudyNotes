package LeetCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class P39_组合总和 {
    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        Arrays.sort(candidates); // 快速排序
        List<List<Integer>> ans = new ArrayList<>();
        process2(candidates, candidates.length - 1, target, new ArrayList(), ans);
        return ans;
    }

    public void process2(int[] c, int r, int target, List<Integer> com, List<List<Integer>> ans) {
        if (target == 0) {
            ans.add(new ArrayList<>(com)); // 注意需要copy一份
            return; // 匹配到
        }
        // 1、找到第一个 <= target的数，并且从该数值开始往下遍历，寻找答案
        for (int i = r; i >= 0; i--) {
            if (c[i] > target) {
                continue;
            }
            if (target - c[i] >= 0) {
                com.add(c[i]); // 添加到末尾
                process2(c, i, target - c[i], com, ans);
                com.remove(com.size() - 1); // 移除最后一个
            }
        }
    }

}
