package LeetCode;

// 插空法：只看奇数，不看偶数
public class P204_计数质数 {

    /**
     * 升级思路：
     * 1. 不要偶数
     * 2. 以i为基数，j=i*i, j+=2*i,将所有奇数情况判断一下，将不符合要求的--
     */
    public int countPrimes(int n) {
        if(n < 3) return 0;
        boolean[] not = new boolean[n]; // 不是素数了
        int count = n / 2; // 1、不要偶数
        // 省略1和2，用N/2 题目是 < n 所以很合理，例如n=7，素数是 2,3,5,7 7/2=3，因此自动过滤了7
        for (int i = 3; i * i < n; i+=2) { // 不要偶数
            if(not[i]){
                continue;
            }
            // 3 -> 3 * 3开始测试， 3*4（偶数）不需要 3*5=15  两者相差 2 * i
            for (int j = i * i; j < n; j += 2 * i) {  // += 2*i
                if(!not[j]){
                    count--;
                    not[j] = true;
                }
            }
        }
        return count;
    }

    // 标记所有不满足要求的数字
    public int countPrimes2(int n) {
        int count = 0;
        int[] num = new int[n + 1];
        for (int i = 2; i < n; i++) {
            if (num[i] == 0) {
                count++;
                // 每个数字 *2 *3 *4 *5 *6
                for (int j = i; j <= n / i; j++) { // j = i性能优化
                    num[j * i] = 1;
                }
            }// == 1直接略过
        }
        return count;
    }

}
