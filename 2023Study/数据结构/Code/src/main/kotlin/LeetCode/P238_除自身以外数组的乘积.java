package LeetCode;

import java.util.Arrays;

/**
 * https://leetcode.cn/problems/product-of-array-except-self
 * 思路：迭代+动态规划
 * 1ms 击败 100.00%使用 Java 的用户
 */
public class P238_除自身以外数组的乘积 {
    public int[] productExceptSelf(int[] nums) {
        int n = nums.length;
        // 装填前面数据
        int[] l = new int[n-1];
        l[0] = nums[0];
        for (int i = 1; i < l.length; i++) {
            l[i] = l[i-1] * nums[i];
        }
        // 装填后面数据
        int[] r = new int[n];
        r[n-1] = nums[n-1];
        for (int i = n-2; i > 0; i--) {
            r[i] = r[i+1]*nums[i];
        }
        int[] ans = new int[n];
        for (int i = 0; i < ans.length; i++) {
            int lt = 1;
            int rt = 1;
            if(i > 0){
                lt = l[i-1];
            }
            if(i < n - 1){
                rt = r[i+1];
            }
            ans[i] = lt * rt;
        }
        // i = 0 =              help[1][n-1]
        // i = 1 = help[0][0] * help[2][n-1]
        // i = 2 = help[0][1] * help[3][n-1]
        // i = 3 = help[0][2] * help[4][n-1];
        // i = 4 = help[0][3] * help[5][n-1];
        // i = n-1 = help[0][n-2];
        return ans;
    }
}

