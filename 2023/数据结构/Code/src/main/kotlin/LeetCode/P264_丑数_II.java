package LeetCode;

import java.util.Arrays;

public class P264_丑数_II {

    /**
     * 1. 注意到了什么？1. 考虑边界 2. 从底往上推导。装填到n
     * 2. 采取了什么行动？
     * 3. 犯了哪些错误？【没有考虑乘法导致int越界】
     * 4. 可以怎样去改变？【int乘法就用long，或者以后所有int都用long】
     *
     * 176 ms 蛮力
     */
    public int nthUglyNumberOld(int n) {
        long dp[] = new long[n + 1];
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 1;
        dp[1] = 1;
        // 丑数 就是质因子只包含 2、3 和 5 的正整数。
        // dp[i] = dp[1...i] x 2、3、5 > dp[i-1]
        // [1, 2, 3, 4, 5, 6, 8, 9, 10, 12]
        for (int i = 2; i <= n; i++) { // 2
            // 找到前驱里面最小的数据
            for (int j = 1; j < i; j++) {
                if (dp[j] * 5 < dp[i - 1]) { // 剪枝
                    continue;
                }
                long t;
                if (dp[j] * 2 > dp[i - 1]) {
                    t = dp[j] * 2;
                } else if (dp[j] * 3 > dp[i - 1]) {
                    t = dp[j] * 3;
                } else if (dp[j] * 5 > dp[i - 1]) {
                    t = dp[j] * 5;
                } else {
                    continue;
                }
                dp[i] = Math.min(dp[i], t);
            }
        }
        return (int) dp[n];
    }

    /**
     * 3 ms
     * 思考：下一个丑数是什么？【多指针方案】
     * 1. 三个指针，指向一个数，每次用过指针往下走。
     * 2. 每次移动时可能出现多个指针同时移动，需要考虑
     */
    public int nthUglyNumber(int n) {
        long dp[] = new long[n + 1];
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 1;
        dp[1] = 1;

        int p2 = 1;
        int p3 = 1;
        int p5 = 1;

        for (int i = 2; i <= n; i++) { // // 2
            long t2 = dp[p2] * 2;
            long t3 = dp[p3] * 3;
            long t5 = dp[p5] * 5;
            if (t2 < t3) {
                if (t2 < t5) {
                    dp[i] = t2;
                    p2++;
                } else if (t2 == t5) {
                    dp[i] = t2;
                    p2++;
                    p5++;
                } else {
                    dp[i] = t5;
                    p5++;
                }
            } else if (t2 == t3) {
                if (t2 < t5) {
                    dp[i] = t2;
                    p2++;
                    p3++;
                } else if (t2 == t5) {
                    dp[i] = t2;
                    p2++;
                    p3++;
                    p5++;
                } else {
                    dp[i] = t5;
                    p5++;
                }
            } else {
                if (t3 < t5) {
                    dp[i] = t3;
                    p3++;
                } else if (t3 == t5) {
                    dp[i] = t3;
                    p3++;
                    p5++;
                } else {
                    dp[i] = t5;
                    p5++;
                }
            }
        }
        return (int) dp[n];
    }

}
