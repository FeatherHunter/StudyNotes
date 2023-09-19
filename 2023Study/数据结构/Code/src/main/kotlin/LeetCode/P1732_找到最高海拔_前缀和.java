package LeetCode;

/**
 * https://leetcode.cn/problems/find-the-highest-altitude
 * 0ms 击败 100.00%使用 Java 的用户
 */
public class P1732_找到最高海拔_前缀和 {
    public int largestAltitude(int[] gain) {

        int[] h = new int[gain.length + 1];
        int max = 0;
        for (int i = 1; i < h.length; i++) {
            h[i] = h[i-1] + gain[i-1];
            max = Math.max(max, h[i]);
        }
        return max;
    }
}
