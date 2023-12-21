package LeetCode;

/**
 * https://leetcode.cn/problems/min-cost-climbing-stairs
 * 0ms 击败 100.00%使用 Java 的用户
 */
public class P746_使用最小花费爬楼梯_动态规划 {
    public int minCostClimbingStairs(int[] cost) {
        // dp[i] 到第i台阶花费的最小费用
        // 1、BaseCase dp[0] = cost[0]
        int[] dp = new int[cost.length + 1]; //
        dp[0] = 0; // 到第0台阶，花费0
        dp[1] = 0; // 到第1台阶，花费0
        // 2、情况 开始爬1个台阶，开始爬2个台阶
        for (int i = 2; i < cost.length + 1; i++) {
            dp[i] = Math.min(dp[i - 2] + cost[i - 2], dp[i - 1] + cost[i - 1]);
        }
        return dp[cost.length];
    }
}
