package LeetCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class P40_组合总和_II {
    public List<List<Integer>> combinationSum2(int[] candidates, int target) {
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
        int last = Integer.MAX_VALUE;
        for (int i = r; i >= 0; i--) {
            if (c[i] > target) {
                continue;
            }
            if (c[i] == last) {
                continue; // 已经处理过相同元素了
            }
            last = c[i];
            if (target - c[i] >= 0) {
                com.add(c[i]); // 添加到末尾
                process2(c, i - 1, target - c[i], com, ans); // i-1代表不能选取重复元素
                com.remove(com.size() - 1); // 移除最后一个
            }
        }
    }

}
