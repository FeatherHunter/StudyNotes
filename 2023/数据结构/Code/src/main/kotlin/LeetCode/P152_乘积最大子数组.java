package LeetCode;

/**
 * **子数组：数字作为结尾**
 * 考虑三种情况，可能会得到最大值
 */
public class P152_乘积最大子数组 {
    // dp动态空间优化

    /**
     * 子数组问题，思想传统：每个数字作为结尾来看
     */
    public int maxProduct(int[] nums) {
        // dp[i] 到index=i的位置数字，最大数据是多少（必须连续，必须以index结尾）
        int n = nums.length;
        int[] dp = new int[2];
        dp[0] = nums[0]; //[][0] 最大值 [][1]最小值
        int max = dp[0];
        int min = dp[0];
        for (int i = 1; i < n; i++) {
            /**
             * 最大值，1-当前值 2-前面最大x自己 3-前面最小x自己
             */
            int dp0 = Math.max(Math.max(dp[0] * nums[i], dp[1] * nums[i]), nums[i]);
            int dp1 = Math.min(Math.min(dp[1] * nums[i], dp[0] * nums[i]), nums[i]);
            max = Math.max(max, dp0); // 找到包含nums[i]的最大值
            min = Math.min(min, dp1); // 找到包含nums[i]的最小值
            dp[0] = dp0;
            dp[1] = dp1;
        }
        return max;
    }

}
