package LeetCode;

public class P122_买卖股票的最佳时机_II {
    // 一维dp
    public int maxProfit(int[] prices) {
        int dp = 0;
        for (int i = 1; i < prices.length; i++) {
            int gap = prices[i] - prices[i - 1];
            dp = dp + (gap > 0 ? (gap) : 0);
        }
        return dp;
    }

}
