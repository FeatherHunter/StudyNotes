package LeetCode;

import java.util.Arrays;

public class P31_下一个排列 {
    public void nextPermutation(int[] nums) {
        int i;
        /**
         * 1、从右到左找到第一个下降的位置i
         */
        for (i = nums.length - 2; i >= 0; i--) {
            if (nums[i] < nums[i + 1]) {
                break;
            }
        }
        /**
         * 2、从右到i找到第一个大于i的数，并且交换
         */
        for (int j = nums.length - 1; j > i && i >= 0; j--) { // i需要合法
            if (nums[j] > nums[i]) {
                int temp = nums[i];
                nums[i] = nums[j];
                nums[j] = temp;
                break;
            }
        }
        /**
         * 3、从右到i，除了i，翻转
         * 4、若从右到左，一个下降都没有，说明是最大字典序，翻转成最小的
         * 两者情况相同：都是从i+1开始反转
         */
        // 双指针
        int l = i + 1;
        int r = nums.length - 1;
        while (l < r) {
            int temp = nums[l];
            nums[l] = nums[r];
            nums[r] = temp;
            l++;
            r--;
        }
    }

}
