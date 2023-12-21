package LeetCode;

public class P509_fibonacci_number {
//    public static int fib(int n) {
//        int a = 0;
//        int b = 1;
//        if(n == 0) return 0;
//        if(n == 1) return 1;
//        for (int i = 2; i <= n; i++) {
//             b = a + b;
//             a = b - a;
//        }
//        return b;
//    }

    /**
     * 0 ms 击败 100.00% 使用 Java 的用户
     */
    public int fib(int n) {
        if(n==0) return 0;
        if(n==1) return 1;
        int a = 0;
        int dp = 1;
        // 从低到顶部，动态规划。填充dp[]数组
        for(int i = 2; i <= n; i++){
            dp = a + dp;
            a = dp - a;
        }
        return dp;
    }

}
