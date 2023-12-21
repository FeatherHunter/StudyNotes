package LeetCode;

public class P474_一和零 {
    public int findMaxForm(String[] strs, int m, int n) {
// 1、dp构造
        int[][] dp = new int[m + 1][n + 1];
        for (String str : strs) {
// 1、计算0 1 长度
            int zeros = 0;
            int ones = 0;
            char[] s = str.toCharArray();
            for (int k = 0; k < s.length; k++) {
                if (s[k] == '0') {
                    zeros++;
                } else {
                    ones++;
                }
            }

            for (int i = m; i >= zeros; i--) {
                for (int j = n; j >= ones; j--) {
                    dp[i][j] = Math.max(dp[i][j], dp[i - zeros][j - ones] + 1);
                }
            }
        }
        return dp[m][n];
    }

}
