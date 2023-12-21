package LeetCode;

/**
 * https://leetcode.cn/problems/n-th-tribonacci-number
 * 0ms 击败 100.00%使用 Java 的用户
 */
public class P1137_第N个泰波那契数 {
    public int tribonacci(int n) {
        if(n == 0) return 0;
        if(n == 1) return 1;
        if(n == 2) return 1;
        int t0 = 0;
        int t1 = 1;
        int t2 = 1;
        // 1,1,2,4,7
        // t0 = 0,t1 = 1, t2 = 1
        // t0 = 1,t2 = 1, t3 = 2
        for (int i = 3; i <= n; i++) {
            int ans = t0 + t1 + t2;
            t0 = t1;
            t1 = t2;
            t2 = ans;

        }
        return t2;
    }
}
