package LeetCode;

public class P97_交错字符串 {
    public static boolean isInterleave(String s1, String s2, String s3) {
        if (s3.length() != s1.length() + s2.length()) return false;
        // dp[i][j] s1拿前i个字符，s2拿前j个字符，构成s3前(i+j)个字符
        boolean[][] dp = new boolean[s1.length() + 1][s2.length() + 1];
        for (int i = 0; i < dp.length; i++) {
            for (int j = 0; j < dp[i].length; j++) {
                if (i == 0) {
                    if (j == 0) {
                        dp[0][0] = true;
                    } else { // 没有s1
                        dp[0][j] = dp[0][j - 1] && s2.charAt(j - 1) == s3.charAt(j - 1);
                    }
                } else {
                    if (j == 0) { // 没有s2
                        dp[i][0] = dp[i - 1][0] && s1.charAt(i - 1) == s3.charAt(i - 1);
                    } else {
                        // 需要考虑的是从上，从左，两种情况，满足一个就可以
                        dp[i][j] = (dp[i - 1][j] && (s1.charAt(i - 1) == s3.charAt(i + j - 1)))
                                || (dp[i][j - 1] && (s2.charAt(j - 1) == s3.charAt(i + j - 1)));
                    }
                }
            }
        }
        return dp[s1.length()][s2.length()];
    }
}
