package LeetCode;

import java.util.HashSet;

public class P128_最长连续序列 {
    /**
     * 思路：HashSet去重和保存
     * 1. 再次遍历，当前数字是开头时，进行遍历查询长度count，如果count>max，记录下最大值
     */
    public int longestConsecutive(int[] nums) {
        HashSet<Integer> set = new HashSet<>();
        for (int i = 0; i < nums.length; i++) {
            set.add(nums[i]);
        }
        int max = 0;
        for (int i = 0; i < nums.length; i++) {
            int num = nums[i];
            // 检查是否存在 num-1,-1的话不会进行遍历
            if (set.contains(num - 1)) {
                continue;
            } else {
                // 没有更小数字，需要遍历得到长度
                int count = 0;
                while (set.contains(num)) {
                    count++;
                    num++;
                }
                if (max < count) {
                    max = count;
                }
            }
        }
        return max;
    }

}
