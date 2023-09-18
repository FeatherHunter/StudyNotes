package LeetCode;

/**
 * https://leetcode.cn/problems/counting-bits
 */
public class P338_比特位计数 {
    public int[] countBits(int n) {

        int[] ans = new int[n + 1];
        for (int i = 0; i <= n; i++) {
            ans[i] = counts(i);
        }
        return ans;
    }
    public int counts(int i){
        int count = 0;
        for (int j = 0; j < 32; j++) {
            if(((i >> j) & 1) == 1){
                count++;
            }
        }
        return count;
    }
}
