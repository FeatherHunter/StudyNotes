package LeetCode;

/**
 * 🥇数组压缩技巧
 * 长方形以某一行为底
 * ["1","0","1","0","0"] => ["1","0","1","0","0"]
 * ["1","0","1","1","1"] => ["2","0","2","1","1"] 底为0的就是0
 * ["1","1","1","1","1"] => ["3","1","3","2","2"]
 * ["1","0","0","1","0"] => ["4","0","0","3","0"] 底为0的就是0
 * 压缩数组
 * 🥹 3ms 击败 98.51%使用 Java 的用户
 * https://leetcode.cn/problems/maximal-rectangle/
 *
 * 时间复杂度：O(n行) * O(m列) = O(n*m)
 */
public class P85_最大矩形 {
    public static int maximalRectangle(char[][] matrix) {
        int[][] m = new int[matrix.length][matrix[0].length];
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if(i == 0){
                    m[i][j] = matrix[i][j] - '0';
                }else{
                    m[i][j] = (matrix[i][j] == '0') ? 0 : m[i-1][j] + 1;
                }
            }
            max = Math.max(max, largestRectangleArea2(m[i]));
        }
        return max;
    }

    public static int largestRectangleArea2(int[] heights) {
        int n = heights.length;
        int[] stack = new int[n];
        int index = -1;
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < n; i++) {
            while ((index != -1)&&(heights[stack[index]] >= heights[i])){
                int cur = stack[index--];
                int ans = (i - ((index == -1) ? -1 : stack[index]) - 1) * heights[cur];
                max = Math.max(max, ans);
            }
            stack[++index] = i;
        }
        while (index != -1){
            int cur = stack[index--];
            int ans = (n - ((index == -1) ? -1 : stack[index]) - 1) * heights[cur];
            max = Math.max(max, ans);
        }
        return max;
    }
}
