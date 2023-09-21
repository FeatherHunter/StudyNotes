package LeetCode;

public class P72_编辑距离 {
    public int minDistance(String word1, String word2) {
        return minDistance(word1, word2, 1, 1, 1);
    }

    /**
     * 样本对应模型：根据结尾处，判断要怎么划分可能性。
     */
    public int minDistance(String word1, String word2, int ac, int dc, int rc) {
        // 1、basecase两种
        int N = word1.length() + 1;
        int M = word2.length() + 1;
        char[] s1 = word1.toCharArray();
        char[] s2 = word2.toCharArray();
        int[][] dp = new int[N][M];
        for (int i = 0; i < M; i++) {
            dp[0][i] = ac * i; // 添加操作
        }
        for (int i = 0; i < N; i++) {
            dp[i][0] = dc * i; // 删除操作
        }
        for (int i = 1; i < N; i++) {
            for (int j = 1; j < M; j++) {
                // 2、普通情况
                // 1.1 dp[i-1][j-1]   【最后一个字符相同】操作：只需要变前面
                // 1.2 dp[i-1][j-1] + r 【最后一个字符不同】操作：变前面，再替换
                // 2. dp[i-1][j] + d 操作：将[i-1]（自己的前面）变成目标j，再删除字符
                // 3. dp[i][j-1] + a 操作：将i自己变成[j-1](目标的前部分)，再添加字符
                // 三种找最小
                // 1.1
                if (s1[i - 1] == s2[j - 1]) { // 字符串下标-1
                    dp[i][j] = dp[i - 1][j - 1]; // 改变
                } else { // 1.2
                    dp[i][j] = dp[i - 1][j - 1] + rc; // 改变 + 替换
                }
                dp[i][j] = Math.min(dp[i][j], dp[i - 1][j] + dc);// 2.删除
                dp[i][j] = Math.min(dp[i][j], dp[i][j - 1] + ac);// 3.增加
            }
        }
        return dp[word1.length()][word2.length()];
    }
}
