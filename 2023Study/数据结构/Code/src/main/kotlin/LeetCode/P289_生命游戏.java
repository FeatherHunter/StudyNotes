package LeetCode;

public class P289_生命游戏 {
    /**
     * 技巧：原数值为0和1，int包含更多位。借助多余位来保存信息。做到原地排序
     * 0ms 击败 100.00%使用 Java 的用户
     */
    public void gameOfLife(int[][] board) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                int count1 = count1(board, i, j);
                if (count1 == 3 || (count1 == 2 && f(board, i, j) == 1)) {
                    board[i][j] |= 2;
                }
            }
        }
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                board[i][j] >>= 1;
            }
        }
    }

    // 获得周围1的数量
    public int count1(int[][] board, int i, int j) {
        return f(board, i - 1, j - 1)
                + f(board, i - 1, j)
                + f(board, i - 1, j + 1)
                + f(board, i, j - 1)
                + f(board, i, j + 1)
                + f(board, i + 1, j - 1)
                + f(board, i + 1, j)
                + f(board, i + 1, j + 1);
    }

    public int f(int[][] board, int i, int j) {
        return (i < 0 || i >= board.length || j < 0 || j >= board[i].length) ? 0 : (board[i][j] & 1);
    }

}
