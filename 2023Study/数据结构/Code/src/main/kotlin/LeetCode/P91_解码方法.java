package LeetCode;

public class P91_解码方法 {
    /**
     * 思路：第i个字符时，方案等于 dp[i-1](需要满足不为0) + dp[i-2](需要满足26以内，并且前一个字符不为0)，否则为0(代表不合法)
     */
    public int numDecodings(String s) {
        char[] w = s.toCharArray();
        int N = w.length + 1;
        // dp[i] w的第i个字符时，有几个方案
        int[] dp = new int[N];
        dp[0] = 1; // 空字符就有1个方案了
        for (int i = 1; i < N; i++) {
            if (w[i - 1] != '0') {
                // 不为0，就有一个独立方案，数量等于dp[i-1]的
                dp[i] += dp[i - 1];
            }
            if (i - 2 >= 0 && w[i - 2] != '0' // 前一个字符不能为0
                    && ((w[i - 1] - '0') + (w[i - 2] - '0') * 10) <= 26) { // 26范围以内
                dp[i] += dp[i - 2]; // 由dp[i-1] + '26' 相同或苏亮方案组成
            }
        }
        return dp[w.length];
    }

}
