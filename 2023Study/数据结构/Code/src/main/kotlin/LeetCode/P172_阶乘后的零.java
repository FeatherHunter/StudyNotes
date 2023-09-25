package LeetCode;

public class P172_阶乘后的零 {
    // 1*2*3*4*5*6*7*...
    // 分解后2的数量 > 5的数量
    // 2*5 = 10 结果为找到几个5
    public int trailingZeroes(int n) {
        int count = 0;
        for (int i = 0; i <= n; i++) {
            int num = i;
            while (num >= 5 && num % 5 == 0) {
                num = num / 5;
                count++;
            }
        }
        return count;
    }

}
