package LeetCode;

public class P494_目标和_样本对应模型 {
    public int findTargetSumWays(int[] nums, int target) {
        if (nums == null) return 0;
        int sum = 0;
        for (int i = 0; i < nums.length; i++) {
            nums[i] = Math.abs(nums[i]);
            sum += nums[i]; // 正数
        }
        target = Math.abs(target); // 正数
        if (target > sum) return 0;
        if ((target + sum) % 2 != 0) {
            // P的集合是小数，怎么可能
            return 0;
        }
        target = (target + sum) / 2;
        // i 0~1000，target 0~sum
        // 二维DP空间优化
        int[] dp = new int[sum + 1];
        dp[0] = 1;
        for (int num : nums) { //将i的循环，都变成数字num
            // 1. i这里就是j，就是target，dp[target] += dp[target-num]
            // 2. dp[i] = dp[i] + dp[i-num] = 不使用num，上层的dp[i] + 使用num，上层的dp[i-num]
            for (int i = sum; i >= num; i--) { // 3. 为什么从sum开始？因为随着sum变小，条件可能越来越不符合
                dp[i] += dp[i - num]; //
            }
        }
        return dp[target];
    }
}
