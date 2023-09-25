package LeetCode;

public class P37_解数独 {
    public void solveSudoku(char[][] board) {
        // 题目保证合法
        boolean[][] row = new boolean[9][10];
        boolean[][] cow = new boolean[9][10];
        boolean[][] bucket = new boolean[9][10];
        // 1、初始化
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                char c = board[i][j];
                if (c < '0' || c > '9') {
                    continue;
                }
                int b = 3 * (i / 3) + j / 3;
                int num = c - '0';
                row[i][num] = cow[j][num] = bucket[b][num] = true;
            }
        }
        // 2、dfs
        dfs(board, 0, 0, row, cow, bucket);
    }

    // 深度优先遍历
    // 从i和j开始深度优先遍历
    public boolean dfs(char[][] board, int i, int j, boolean[][] row, boolean[][] cow, boolean[][] bucket) {
        if (j == 9) {
            return true;
        }
        int nexti = (i < 8) ? i + 1 : 0;
        int nextj = (i < 8) ? j : j + 1;
        if (board[i][j] != '.') {
            return dfs(board, nexti, nextj, row, cow, bucket);
        }
        int b = 3 * (i / 3) + j / 3;
        for (int num = 1; num <= 9; num++) {
            if (row[i][num] == false && cow[j][num] == false && bucket[b][num] == false) {
                row[i][num] = cow[j][num] = bucket[b][num] = true;//占据
                board[i][j] = (char) (num + '0');
                if (dfs(board, nexti, nextj, row, cow, bucket)) {
                    return true; // 满足了
                }
                // 不满足
                board[i][j] = '.'; // 还原
                row[i][num] = cow[j][num] = bucket[b][num] = false;//占据
            } else {
                continue; // 不考虑
            }
        }
        return false; // 一个满足的都没找到
    }

}
