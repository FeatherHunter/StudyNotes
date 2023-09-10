package LeetCode;

import java.util.Arrays;

public class P31_下一个排列 {
    // 123
    // 132
    // 213 // 231
    // 231
    // 312
    // 321
    public void nextPermutation(int[] nums) {

        // TODO 没有通过！！！！！！！！！！
        if (nums == null || nums.length < 2) return;
        int n = nums.length;
        int i = n - 1;
        while (i > 0){
            for (int j = i-1; j >=0; j--) {
                if(nums[j] < nums[i]){

                    // 找到咯
                    // 说明i的数据需要到j去
                    int temp = nums[i];
                    for (int k = i-1; k >= j; k--) {
                        nums[k + 1] = nums[k];
                    }
                    nums[j] = temp;
                    return;
                }
            }
            i--;
        }
        // 走到这里说明是反序列的，需要重置
        for (i = 0; i < n / 2; i++) {
            swap(nums, i, n - 1 - i);
        }
    }
    public static void swap(int[] nums, int i, int j){
        if(i == j) return;
        nums[i] = nums[i] ^ nums[j];
        nums[j] = nums[i] ^ nums[j];
        nums[i] = nums[i] ^ nums[j];
    }
}
