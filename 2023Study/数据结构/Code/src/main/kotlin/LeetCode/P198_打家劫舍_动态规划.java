package LeetCode;

/**
 * https://leetcode.cn/problems/house-robber
 * 0ms 击败 100.00%使用 Java 的用户
 */
public class P198_打家劫舍_动态规划 {
    public int rob(int[] nums) {
        // dp[i] 光顾完i房间，拿到的最多的钱是多少
        int[] dp = new int[nums.length];
        dp[0] = nums[0];
        if(nums.length == 1) return dp[0];
        dp[1] = Math.max(dp[0], nums[1]);
        if(nums.length == 2) return dp[1];
        dp[2] = Math.max(dp[1], dp[0] + nums[2]);
        if(nums.length == 3) return dp[2];
        for (int i = 3; i < nums.length; i++) {
            // 1、需要这个房间的钱
            // 来源于 i - 2房间
            // 来源于 i - 3房间
            dp[i] = Math.max(dp[i-2], dp[i-3]) + nums[i]; // 要拿钱
            // 2、不需要这个房间的钱
            // 来源与 i - 1房间
            dp[i] = Math.max(dp[i-1], dp[i]); // 取最大值
        }
        return dp[nums.length - 1];
    }
}
