package LeetCode;

/**
 * 878. 第 N 个神奇数字
 * https://leetcode.cn/problems/nth-magical-number/
 */
public class P878_nth_magical_number {
    public int nthMagicalNumber(int n, int a, int b) {
        long max = Math.min(a, b) * (long)n; // 随时避免溢出
        long l = 0;
        long r = max;
        long mid = 0;
        long count;
        long lcm = lcm(a, b);
        long ans = 0;
        while (l <= r){
            mid = l + (r-l) / 2;
            count = mid / a + mid / b - mid / lcm;
            if(count >= n){
                r = mid - 1;
                ans = mid;
            }else{
                l = mid + 1;
            }
        }
        return (int) (ans%1000000007);
    }


    public static long lcm(long a, long b){
        return a / gcd(a, b) * b;
    }
    public static long gcd(long a, long b){
        return (b == 0) ? a : gcd(b, a % b);
    }
}
