package LeetCode;

public class P53_最大子数组和 {
    // 空间优化
    public int maxSubArray(int[] nums) {
        int dp = nums[0];
        int max = dp;
        for (int i = 1; i < nums.length; i++) {
            // 当前位置结尾，和，要么是抛弃前面的和自己独立nums[i]，要么结合前面dp[i-1]+nums[i]
            dp = (dp < 0) ? nums[i] : (dp + nums[i]);
            max = Math.max(max, dp); // 新dp
        }
        return max;
    }
}
