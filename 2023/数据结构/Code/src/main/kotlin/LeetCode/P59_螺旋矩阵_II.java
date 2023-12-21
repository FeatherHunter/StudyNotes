package LeetCode;

public class P59_螺旋矩阵_II {
    // 0ms 击败 100.00%使用 Java 的用户
    int iadd = 0;
    int jadd = 1;
    public static final int RIGHT = 0;
    public static final int LEFT = 1;
    public static final int DOWN = 2;
    public static final int UP = 3;
    public int status = RIGHT;

    public int[][] generateMatrix(int n) {
        int[][] r = new int[n][n];
        int i = 0;
        int j = 0;
        int num = 1;
        while (true) {
            r[i][j] = num++;
            int ti = i + iadd;
            int tj = j + jadd;
            if ((ti < 0 || ti >= n || tj < 0 || tj >= n || r[ti][tj] != 0)) {
                switchStatus();
                ti = i + iadd;
                tj = j + jadd;
            }
            if ((ti < 0 || ti >= n || tj < 0 || tj >= n || r[ti][tj] != 0)) {
                // 还不满足条件，需要结束了
                return r;
            }
            i = ti;
            j = tj;
        }
    }

    public void switchStatus() {
        switch (status) {
            case RIGHT:
                down();
                break;
            case DOWN:
                left();
                break;
            case LEFT:
                up();
                break;
            case UP:
                right();
                break;
        }
    }

    public void right() {
        iadd = 0;
        jadd = 1;
        status = RIGHT;
    }

    public void left() {
        iadd = 0;
        jadd = -1;
        status = LEFT;
    }

    public void down() {
        iadd = 1;
        jadd = 0;
        status = DOWN;
    }

    public void up() {
        iadd = -1;
        jadd = 0;
        status = UP;
    }

}
