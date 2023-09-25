package LeetCode;

// 插空法
public class P204_计数质数 {
    // 标记所有不满足要求的数字
    public int countPrimes(int n) {
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
