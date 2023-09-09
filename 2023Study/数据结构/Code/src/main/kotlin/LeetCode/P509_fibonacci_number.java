package LeetCode;

public class P509_fibonacci_number {
    public static int fib(int n) {
        int a = 0;
        int b = 1;
        if(n == 0) return 0;
        if(n == 1) return 1;
        for (int i = 2; i <= n; i++) {
             b = a + b;
             a = b - a;
        }
        return b;
    }
}
