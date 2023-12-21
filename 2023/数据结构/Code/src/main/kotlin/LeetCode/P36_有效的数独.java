package LeetCode;

public class P36_有效的数独 {
    // 行映射map，key（下标）=数字，value=是否已经有了
    // 列映射map，key（下标）=数字，value=是否已经有了
    // 3x3桶map
    // 注意：要考虑空白字符
    public boolean isValidSudoku(char[][] board) {
        boolean[][] row = new boolean[9][10];
        boolean[][] cow = new boolean[9][10];
        boolean[][] bucket = new boolean[9][10];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                char c = board[i][j];
                if (c < '0' || c > '9') {
                    continue;
                }
                int b = 3 * (i / 3) + j / 3;
                int num = c - '0';
                if (row[i][num] || cow[j][num] || bucket[b][num]) {
                    return false;
                }
                row[i][num] = cow[j][num] = bucket[b][num] = true;
            }
        }
        return true;
    }

}
