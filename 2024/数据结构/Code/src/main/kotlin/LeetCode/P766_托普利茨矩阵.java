package LeetCode;

public class P766_托普利茨矩阵 {
    public boolean isToeplitzMatrix(int[][] matrix) {
        int last = Math.min(matrix.length, matrix[0].length);
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] == -1){
                    continue;
                }
                int flag = matrix[i][j];
                for (int k = 0; k < last && (i+k < matrix.length) && (j+k < matrix[0].length); k++) {
                    if(matrix[i+k][j+k] != flag){
                        return false;
                    }else{
                        matrix[i+k][j+k] = -1;
                    }
                }
            }
        }
        return true;
    }
}
