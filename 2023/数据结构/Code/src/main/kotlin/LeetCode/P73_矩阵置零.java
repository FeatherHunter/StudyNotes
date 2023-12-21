package LeetCode;

public class P73_矩阵置零 {
    /**
     * 将矩阵0行0列，作为标记位置。代表：某行某列都要设置为0，
     * 额外增加两个标志位，cow：=1代表第0列需要为0，row：=1代表第0行需要为0
     * 【进一步优化】[0][0]只代表第0行是否为0，第0列用boolean flag表示是否为0
     */
    public void setZeroes(int[][] matrix) {
        // 1、判断第0行，第0列是否要归0；
        boolean row = false;
        boolean cow = false;
        for (int i = 0; i < matrix.length; i++) {
            if (matrix[i][0] == 0) {
                cow = true;//cow：=1代表第0列需要为0
            }
        }
        for (int j = 0; j < matrix[0].length; j++) {
            if (matrix[0][j] == 0) {
                row = true;//=1代表第0行需要为0
            }
        }
        // 2、统计出需要变0的行和列
        for (int i = 1; i < matrix.length; i++) {
            for (int j = 1; j < matrix[i].length; j++) {
                if (matrix[i][j] == 0) {
                    matrix[0][j] = 0;
                    matrix[i][0] = 0;
                }
            }
        }
        // 3、行列变0
        for (int i = 1; i < matrix.length; i++) {
            for (int j = 1; j < matrix[i].length; j++) {
                if (matrix[i][0] == 0 || matrix[0][j] == 0) {
                    matrix[i][j] = 0;
                }
            }
        }
        // 4、0行0列
        if (cow) {
            for (int i = 0; i < matrix.length; i++) {
                matrix[i][0] = 0;
            }
        }
        if (row) {
            for (int j = 0; j < matrix[0].length; j++) {
                matrix[0][j] = 0;
            }
        }
    }

}
