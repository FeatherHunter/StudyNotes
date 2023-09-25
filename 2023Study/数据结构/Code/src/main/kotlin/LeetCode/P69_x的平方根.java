package LeetCode;

// [LCR 072. x 的平方根](https://leetcode.cn/problems/jJ0w9p/)
public class P69_x的平方根 {
    // 二分法
    public int mySqrt(int x) {
        if (x == 0) return 0;
        int l = 1;
        int r = x;
        int m;
        int ans = 1;
        while (l <= r) {
            m = l + (r - l) / 2;
            if ((long) m * m > x) {
                r = m - 1;
            } else { // <= //
                ans = m;
                l = m + 1;
            }
        }
        return ans;
    }

}
