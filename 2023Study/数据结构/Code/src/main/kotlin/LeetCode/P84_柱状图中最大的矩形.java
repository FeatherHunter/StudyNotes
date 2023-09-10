package LeetCode;

import java.util.Arrays;

/**
 * https://leetcode.cn/problems/largest-rectangle-in-histogram/
 * 6ms 击败 99.59%使用 Java 的用户
 */
public class P84_柱状图中最大的矩形 {
    // 升级版本，融入答案
    public int largestRectangleArea2(int[] heights) {
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
    // 左侧到右侧，乘以，自身高度 = 面积
    public int largestRectangleArea(int[] heights) {
        int n = heights.length;
        int[] stack = new int[n];
        int[] left = new int[n];
        int[] right = new int[n];
        int index = -1;
        for (int i = 0; i < n; i++) {
            while ((index != -1)&&(heights[stack[index]] >= heights[i])){
                int cur = stack[index--];
                left[cur] = (index == -1) ? -1 : stack[index];
                right[cur] = i;
            }
            stack[++index] = i;
        }
        while (index != -1){
            int cur = stack[index--];
            left[cur] = (index == -1) ? -1 : stack[index];
            right[cur] = n;
        }
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < n; i++) {
            int ans = (right[i] - left[i] - 1) * heights[i];
            max = Math.max(max, ans);
        }
        return max;
    }
}
