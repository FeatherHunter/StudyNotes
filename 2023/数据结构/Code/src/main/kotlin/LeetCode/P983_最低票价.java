package LeetCode;

import java.util.Arrays;

// https://leetcode.cn/problems/minimum-cost-for-tickets/description/
// 动态规划P983
public class P983_最低票价 {
    /**
     *
     * @param days 该数组中每个天都旅游过
     * @param costs 只有三个数，代表：1天7天30天
     * @return 最小总消费
     */
    public static int durations[] = {1, 7, 30}; // 用于获取数组数量
    public static int dp[] = new int[366]; // 最多365天，需要+1
    public int mincostTickets(int[] days, int[] costs) {
        int n = days.length;
        // 填充为最大值
        Arrays.fill(dp, 0, n + 1, Integer.MAX_VALUE);
        // 末尾
        dp[n] = 0; // 末尾不存在的天数，消耗为0
        // 从末尾往前推导
        for (int i = n - 1; i >= 0; i--){
            // 到i为止，计算三天票价开销，用哪个最值得
            for (int k = 0, j = i; k < 3; k++) {
                // k代表当前买的是1天，7天还是30天
                while (j < n && days[i] + durations[k] > days[j]){
                    j++; // days[i]当前天数 + 间隔时间 > days[j]目标天数
                }
                dp[i] = Math.min(dp[i], costs[k] + dp[j]);
            }
        }
        return dp[0];
    }
}
