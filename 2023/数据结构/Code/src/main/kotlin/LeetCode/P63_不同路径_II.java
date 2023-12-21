package LeetCode;

public class P63_不同路径_II {
    // dp[n][m]而不是dp[n+1][m+1]
    public int uniquePathsWithObstacles(int[][] obstacleGrid) {
        /**
         * dp[i][j]的路径有多少？
         * 1）(arr[i-2][j-1] == 1)?0:dp[i-1][j]
         * 2）(arr[i-1][j-2] == 1)?0:dp[i][j-1]
         * 3) arr[i-1][j-1] == 1, dp[i][j] = 0
         * dp[i][j] = (arr[i-1][j-1] == 1)?0:(【1】+【2】)
         *
         * 基本：
         * 1. dp[0][0]需要考虑arr[0][0]是否为0
         * 2. i=0时，dp[0][j] = (arr[0][j] == 1)?0:(dp[0][j-1]);//当前节点和左边
         * 3. j=1时，dp[i][0] = (arr[i][0] == 1)?0:(dp[i-1][0]);//当前节点和上面
         */
        int n = obstacleGrid.length;
        int m = obstacleGrid[0].length;
        int[][] dp = new int[n][m];
        dp[0][0] = (obstacleGrid[0][0] == 1) ? 0 : 1; // BaseCase需要考虑清除
        for (int j = 1; j < m; j++) {
            dp[0][j] = (obstacleGrid[0][j] == 1) ? 0 : (dp[0][j - 1]); // 0或者1
        }
        for (int i = 1; i < n; i++) {
            dp[i][0] = (obstacleGrid[i][0] == 1) ? 0 : (dp[i - 1][0]); // 0或者1
        }
        for (int i = 1; i < n; i++) {
            for (int j = 1; j < m; j++) {
                int left = (obstacleGrid[i - 1][j] == 1) ? 0 : dp[i - 1][j];
                int top = (obstacleGrid[i][j - 1] == 1) ? 0 : dp[i][j - 1];
                dp[i][j] = (obstacleGrid[i][j] == 1) ? 0 : (left + top);
            }
        }
        return dp[n - 1][m - 1];
    }

}
