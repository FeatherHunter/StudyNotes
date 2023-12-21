package LeetCode;

/**======================================
 * 0ms 击败 100.00%使用 Java 的用户
 * 环形房子：dp[i]: arr[0..i]范围上，随意选择。任何两数不能相邻，最大累加和是多少？
 * 相比于P198:
 * 1）0~N-2取值
 * 2）1~N-1取值
 * 3) 比较max
 *=======================================*/
public class P213_打家劫舍II_动态规划 {
    public int rob(int[] nums) {
        int n = nums.length;
        if(n == 1) return nums[0];
        if(n == 2) return Math.max(nums[0], nums[1]);
        int[] dp = new int[n];
        // 1、0~N-2取最大值
        dp[0] = nums[0];
        dp[1] = Math.max(nums[0], nums[1]);
        for (int i = 2; i < n - 1; i++) {
            dp[i] = Math.max(Math.max(nums[i], dp[i-1]), dp[i-2]+nums[i]);
        }
        int max1 = dp[n-2];
        // 2、1~N-1取最大值
        dp[1] = nums[1];
        dp[2] = Math.max(nums[1], nums[2]);
        for (int i = 3; i < n; i++) {
            dp[i] = Math.max(Math.max(nums[i], dp[i-1]), dp[i-2]+nums[i]);
        }
        int max2 = dp[n-1];
        // 3、两者取最大值
        return Math.max(max1, max2);
    }
}
