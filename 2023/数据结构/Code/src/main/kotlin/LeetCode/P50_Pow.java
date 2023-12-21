package LeetCode;

// 所有会突破int上下限的都用long完成
public class P50_Pow {
    // 实现 pow(x, n) ，即计算 x 的整数 n 次幂函数（即，xn ）。
    public double myPow(double x, int n) {
        /**
         * 10的75次方，如何计算最快？
         * 75拆为64,8,2,1 => 1001011
         * t = 10.
         * 第一位=1,1 * t，t = t^2
         * 第二位=1,1 * t
         */
        // 1、n为正数
        double t = x;
        double res = 1;
        // 看完32位
        boolean neg = (n < 0);
        long nl = n;
//    n = Math.abs(n); // 错误！需要考虑-2147483648
        nl = Math.abs(nl);
        while (nl > 0) {
            if ((nl & 1) == 1) {
                res *= t;
            }
            t *= t; // t翻倍
            nl >>= 1; // 不断右移
        }
        // 2、n为负数,1/结果
        if (neg) {
            res = 1 / res;
        }
        return res;
    }

}
