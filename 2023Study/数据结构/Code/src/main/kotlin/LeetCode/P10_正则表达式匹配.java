package LeetCode;

public class P10_正则表达式匹配 {
    public boolean isMatch(String s1, String p1) {
        // dp[i][j] // s从[0,i]和p从[0,j]的是否可以匹配上
        /**
         * p[j]不为*
         * 1) dp[i][j] = match(i, j) && dp[i-1][j-1]
         * // *最少是第2个
         * 2) dp[i][j] = dp[i][j-2] || (match(i, j-1) && dp[i-1][j])
         */
        char[] s = s1.toCharArray();
        char[] p = p1.toCharArray();
        int N = s.length + 1;
        int M = p.length + 1;
        boolean[][] dp = new boolean[N][M];
        dp[0][0] = true;
        for (int i = 0; i < N; i++) {
            for (int j = 1; j < M; j++) { // j最少从第1个开始
                if (p[j - 1] != '*') {
                    dp[i][j] = match(s, p, i, j) && dp[i - 1][j - 1]; // match在前可以避免i=0
                } else { // *最少是第2个
                    dp[i][j] = dp[i][j - 2] || (match(s, p, i, j - 1) && dp[i - 1][j]); // i都是 >= 0因此，不会有问题
                }
            }
        }
        return dp[s.length][p.length];
    }

    public boolean match(char[] s, char[] p, int i, int j) {
        if (i == 0) {
            return false;
        }
        if (p[j - 1] == '.') {
            return true;
        }
        return s[i - 1] == p[j - 1];
    }

}
