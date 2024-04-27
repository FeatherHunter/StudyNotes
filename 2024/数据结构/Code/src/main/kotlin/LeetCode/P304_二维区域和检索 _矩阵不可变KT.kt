package LeetCode

class P304_二维区域和检索_矩阵不可变KT {
    class NumMatrix(private val matrix: Array<IntArray>) {

        // 每行先算好累加和
        val rowsum = Array<IntArray>(matrix.size){IntArray(matrix[0].size)}

        init {
            for (i in 0..matrix.size-1){
                rowsum[i][0] = matrix[i][0]
                for(j in 1..matrix[0].size-1){
                    rowsum[i][j] = rowsum[i][j-1] + matrix[i][j]
                }
            }
        }

        fun sumRegion(row1: Int, col1: Int, row2: Int, col2: Int): Int {
            var sum = 0
            for(i in row1..row2){
                sum += rowsum[i][col2] - if(col1 == 0) 0 else rowsum[i][col1-1]
            }
            return sum
        }
    }
}