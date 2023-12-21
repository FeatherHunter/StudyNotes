package LeetCode;

public class P221_最大正方形 {

    /**
     * dp[i][j] 截止到 i和j的位置的最大正方形面积
     * ["1","0","1","0","0"]
     * ["1","0","1","1","1"]
     * ["1","1","1","1","1"]
     * ["1","0","0","1","0"]
     */
    public int maximalSquare(char[][] matrix) {
        int N = matrix.length;
        int M = matrix[0].length;

        int[][] dp = new int[N + 1][M + 1];
        dp[0][0] = 0;
        int max = 0;
        // 除了为0的地方，其他的都要在wile中装填
        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= M; j++) {
                if(i == 1){
                    dp[1][j] = (matrix[0][j-1] == '1') ? 1 : 0;
                }else if(j == 1){
                    dp[i][1] = (matrix[i-1][0] == '1') ? 1 : 0; // 装填边缘
                }else if(matrix[i-1][j-1] == '1'){
                    int dppre = dp[i-1][j-1];
                    dp[i][j] = 1;
                    if(dppre > 0){
                        if(dp[i][j-1] >= dppre && dp[i-1][j] >= dppre){
                            dp[i][j] = (dppre + (int)Math.sqrt(dppre) * 2 + 1);
                        }else{
                            int min = Math.min(dp[i][j-1], dp[i-1][j]);
                            dp[i][j] = (min + (int)Math.sqrt(min) * 2 + 1);
                        }
                    }
                }
                max = Math.max(max, dp[i][j]);
            }
        }
        // 找到最大值哦
        return max;
    }
}
