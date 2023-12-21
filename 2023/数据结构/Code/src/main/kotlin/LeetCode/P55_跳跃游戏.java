package LeetCode;

public class P55_跳跃游戏 {
    public boolean canJump(int[] nums) {
        // dp[i] 第i步，可以走出的最大距离
        // dp[i] = dp[i-1]-1 //可能一，和自己没关系，自己算一步
        // dp[i] = dp[i-1]>0? nums[i-1]:0 // 可能二，和自己有关系，需要下面一步可以走到我这里哦
        // BASECASE: dp[0]第0步，一定可以迈出1步
        int[] dp = new int[nums.length];
        dp[0] = 1;//肯定可以买到
        for (int i = 1; i < dp.length ; i++) {
            dp[i] = Math.max(dp[i-1]-1, dp[i-1] > 0 ? nums[i-1] : 0); //
        }
        return dp[nums.length-1] > 0; // 到最后一个下标，因此看第n-1步即可
    }
}
