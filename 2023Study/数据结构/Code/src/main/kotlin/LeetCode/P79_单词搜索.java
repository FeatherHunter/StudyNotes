package LeetCode;

public class P79_单词搜索 {
    // 最初版本 112ms
    //击败 88.43%使用 Java 的用户
    public static boolean exist(char[][] board, String word) {
        char[] w = word.toCharArray();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (w[0] == board[i][j]) {
                    boolean flag = process(board, i, j, w, 0);
                    if (flag) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // 从i,j位置开始匹配w中从t开始的字符串
    public static boolean process(char[][] board, int i, int j, char[] w, int t) {
        if (board[i][j] != w[t]) {
            return false; //匹配不上
        }
        if (t == w.length - 1) {
            // 当前位置匹配上，并且t为最后一个
            // 可以处理“a"和”a“的特殊情况
            return true;
        }
        char temp = board[i][j];
        board[i][j] = 0;
        // 左
        boolean left = false;
        if (j - 1 >= 0 && board[i][j - 1] != 0) {
            left = process(board, i, j - 1, w, t + 1);
        }
        // 右
        boolean right = false;
        if (j + 1 < board[0].length && board[i][j + 1] != 0) {
            right = process(board, i, j + 1, w, t + 1);
        }
        // 上
        boolean top = false;
        if (i - 1 >= 0 && board[i - 1][j] != 0) {
            top = process(board, i - 1, j, w, t + 1);
        }
        // 下
        boolean bot = false;
        if (i + 1 < board.length && board[i + 1][j] != 0) {
            bot = process(board, i + 1, j, w, t + 1);
        }
        board[i][j] = temp;
        return left || right || top || bot;
    }

}
