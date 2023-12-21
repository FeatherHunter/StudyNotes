package LeetCode;

import java.util.Arrays;

public class P329_矩阵中的最长递增路径 {
    /**
     * 7ms 89.41%
     * 错误：忘记了条件判断需要比较大小，列下场景。不能把编码当成场景梳理。
     */
    public int longestIncreasingPath(int[][] matrix) {
        int n = matrix.length;
        int m = matrix[0].length;

        // 缓存每个格子可走的最长增长路径
        int[][] map = new int[n][m];
        for (int i = 0; i < map.length; i++) {
            Arrays.fill(map[i], -1);
        }
        // 遍历所有格子，找到最大值
        int max = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                // 记忆化搜索
                max = Math.max(max, longestIncreasing(matrix, i, j, map));
            }
        }
        return max;
    }

    public int longestIncreasing(int[][] matrix, int i, int j, int[][] map) {
        if (map[i][j] != -1) return map[i][j];
        map[i][j] = 1;
        if (i > 0) {
            if (matrix[i - 1][j] > matrix[i][j])
                map[i][j] = Math.max(map[i][j], longestIncreasing(matrix, i - 1, j, map) + 1);
        }
        if (i < matrix.length - 1) {
            if (matrix[i + 1][j] > matrix[i][j])
                map[i][j] = Math.max(map[i][j], longestIncreasing(matrix, i + 1, j, map) + 1);
        }
        if (j > 0) {
            if (matrix[i][j - 1] > matrix[i][j])
                map[i][j] = Math.max(map[i][j], longestIncreasing(matrix, i, j - 1, map) + 1);
        }
        if (j < matrix[0].length - 1) {
            if (matrix[i][j + 1] > matrix[i][j])
                map[i][j] = Math.max(map[i][j], longestIncreasing(matrix, i, j + 1, map) + 1);
        }
        return map[i][j];
    }

}
