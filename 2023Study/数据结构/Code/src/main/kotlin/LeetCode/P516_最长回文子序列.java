package LeetCode;

public class P516_最长回文子序列 {
    // 16ms击败97.46%使用 Java 的用户
    public int longestPalindromeSubseq(String s) {
        return longestDp(s.toCharArray());
    }

    /**
     * 注意到了什么：需要递归
     * 采取了什么：记忆化搜索
     * 有哪些错误：没发现可以采用int级别的两个变量、画图分析边界不谨慎导致j超过边界
     * 如何改正它：
     * 1. 根据画图，仔细写代码
     * 2. 多做题，积累dp可以转换的经验
     */
    public int longestDp(char[] w) {
        int n = w.length;
        int[][] dp = new int[n][n];

        for (int i = n - 1; i >= 0; i--) {
            dp[i][i] = 1;
            for (int j = i + 1; j < n; j++) {
                if (w[i] == w[j]) {
                    dp[i][j] = 2 + dp[i + 1][j - 1];
                } else {
                    dp[i][j] = Math.max(dp[i + 1][j], dp[i][j - 1]);
                }
            }
        }

        return dp[0][n - 1];
    }

}
