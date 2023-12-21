package LeetCode;

public class PLCR_095_最长公共子序列 {

    /**
     * 执行用时分布:
     * 5ms击败97.03%使用 Java 的用户
     * 消耗内存分布:
     * 43.49MB击败79.45%使用 Java 的用户
     */
    public int longestCommonSubsequence(String text1, String text2) {
        // dp[n][m]
        // dp[i][j] 到text1的下标i的字符和道text2的下标j的字符，拥有最长公共子序列的长度
        /**
         * abcde ace
         * 情况一：
         * i=0||j=0
         *  字符i=字符j,dp[i][j] = 1
         *  字符i！=字符j，dp[i][j] = 0;
         * abcde ace 字符i=字符j，dp[i][j] = dp[i-1][j-1] + 1（i>0,j>0） i=0orj=
         * abcd ace 字符i！=字符j，dp[i][j] 那么，dp[i][j] = Max(dp[i-1][j], dp[i][j-1])
         */
        char[] w1 = text1.toCharArray();
        char[] w2 = text2.toCharArray();

        int n = w1.length;
        int m = w2.length;

        int[][] dp = new int[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {

                if(w1[i] == w2[j]){
                    if(i !=0 && j != 0){
                        dp[i][j] = dp[i-1][j-1] + 1;
                    }else{
                        dp[i][j] = 1;
                    }
                }
                else{
                    if(i !=0 && j != 0){
                        dp[i][j] = Math.max(dp[i-1][j], dp[i][j-1]);
                    }else if(i == 0 && j == 0){
                        dp[i][j] = 0;
                    }else if (i == 0){
                        dp[i][j] = dp[i][j-1];
                    }else{
                        dp[i][j] = dp[i-1][j];
                    }

                }
            }
        }
        return dp[n-1][m-1];
    }
}
