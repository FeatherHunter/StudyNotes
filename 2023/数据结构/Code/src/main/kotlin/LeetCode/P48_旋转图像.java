package LeetCode;

public class P48_旋转图像 {
    public void rotate(int[][] matrix) {
        int n = matrix.length;
        int m = n / 2;
        if (n % 2 == 1) {
            n = n / 2 + 1;
        } else {
            n = n / 2;
        }
        /**
         * 需要注意，横向作为i的情况下，i是matrix[j][i]第二个参数
         * 算好P1 P2 P3 P4坐标，TEMP保存P4。循环赋值后，P1=TEMP
         */
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                int temp = matrix[matrix.length - 1 - i][j];
                matrix[matrix.length - 1 - i][j] = matrix[matrix.length - 1 - j][matrix.length - 1 - i];
                matrix[matrix.length - 1 - j][matrix.length - 1 - i] = matrix[i][matrix.length - 1 - j];
                matrix[i][matrix.length - 1 - j] = matrix[j][i];
                matrix[j][i] = temp;
            }
        }
    }

}
