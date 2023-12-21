package LeetCode;

public class P74_搜索二维矩阵 {
    public boolean searchMatrix(int[][] matrix, int target) {
        int n = matrix.length;
        int m = matrix[0].length;
        int l = 0;
        int r = m - 1;
        int t = 0;
        int d = n - 1;
        int row = 0;
        while (t <= d) {
            int mid = t + (d - t) / 2;
            if (matrix[mid][0] < target) {
                row = mid;
                t = mid + 1;
            } else if (matrix[mid][0] > target) {
                d = mid - 1;
            } else {
                return true;
            }
        }
        // t就是当前符合要求的一行
        while (l <= r) {
            int mid = l + (r - l) / 2;
            if (matrix[row][mid] < target) {
                l = mid + 1;
            } else if (matrix[row][mid] > target) {
                r = mid - 1;
            } else {
                return true;
            }
        }
        return false; // 没找到
    }

}
