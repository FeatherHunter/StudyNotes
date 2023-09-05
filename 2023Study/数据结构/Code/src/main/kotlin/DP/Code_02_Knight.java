package DP;

public class Code_02_Knight {
    /**
     * 给出行和列：n x m
     * 人一开始在的位置（a,b）,每次上下左右走，随机概率，不越界的概率是多少？
     * 688. 骑士在棋盘上的概率：https://leetcode.cn/problems/knight-probability-in-chessboard/
     * */
    public static double knightProbabilityOld(int n, int k, int row, int column) {
        long alive = alive(n, k, row, column);
        long total = (long) Math.pow(8, k);
        return (double)alive/total;
    }

    public static int alive(int n, int k, int row, int column){ // k 剩余步骤
        int res = 0;
        if(row < 0 || row >= n || column < 0 || column >= n){ // 当前点已经死了
            res = 0;
            return res;
        }
        if(k == 0){ // 当前点活了
            res = 1;
            return res;
        }

        res = alive(n, k - 1, row+1, column+2)
            + alive(n, k - 1, row+1, column-2)
            + alive(n, k - 1, row+2, column+1)
            + alive(n, k - 1, row+2, column-1)
            + alive(n, k - 1, row-1, column+2)
            + alive(n, k - 1, row-1, column-2)
            + alive(n, k - 1, row-2, column+1)
            + alive(n, k - 1, row-2, column-1);

        return res;
    }

    public static double knightProbabilityDP(int n, int k, int row, int column) {
        double alive = aliveDp(n, k, row, column);
        double res = alive;
        while(k-->0){
            res /= 8;
        }
        System.out.println(res);
        return res;
    }

    public static double aliveDp(int n, int k, int row, int column){ // k 剩余步骤
        if(row < 0 || row >= n || column < 0 || column >= n){ // 当前点已经死了
            return 0;
        }

        double[][][] help = new double[n][n][k+1];
        for(int i = 0; i < n; i++){ // 大小为0，走个皮
            for (int j = 0; j < n; j++) {
                help[i][j][0] = 1; // k = 0层，都OK
            }
        }

        for (int h = 1; h <= k; h++) {
            for(int i = 0; i < n; i++){ // 大小为0，走个皮
                for (int j = 0; j < n; j++) {
                    help[i][j][h] = getValue(help, n, h - 1, i+1, j+2)
                            + getValue(help, n, h - 1, i+1, j-2)
                            + getValue(help, n, h - 1, i+2, j+1)
                            + getValue(help, n, h - 1, i+2, j-1)
                            + getValue(help, n, h - 1, i-1, j+2)
                            + getValue(help, n, h - 1, i-1, j-2)
                            + getValue(help, n, h - 1, i-2, j+1)
                            + getValue(help, n, h - 1, i-2, j-1);
                }
            }
        }

        return help[row][column][k];
    }

    public static double getValue(double[][][] help, int n, int h, int i, int j){
        if(i < 0 || i >= n || j < 0 || j >= n){ // 当前点已经死了
            return 0;
        }
        return help[i][j][h];
    }

    static int[][] dirs = {{-2, -1}, {-2, 1}, {2, -1}, {2, 1}, {-1, -2}, {-1, 2}, {1, -2}, {1, 2}};

    public static double knightProbability(int n, int k, int row, int column) {
        double[][][] dp = new double[k + 1][n][n];
        for (int step = 0; step <= k; step++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (step == 0) {
                        dp[step][i][j] = 1;
                    } else {
                        for (int[] dir : dirs) {
                            int ni = i + dir[0], nj = j + dir[1];
                            if (ni >= 0 && ni < n && nj >= 0 && nj < n) {
                                dp[step][i][j] += dp[step - 1][ni][nj] / 8;
                            }
                        }
                    }
                }
            }
        }
        return dp[k][row][column];
    }
}
