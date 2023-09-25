package LeetCode;

import java.util.Arrays;

public class P1679_K和数对的最大数目 {
    // 双指针
    public int maxOperations(int[] nums, int k) {
        int op = 0;
        Arrays.sort(nums);
        int l = 0;
        int r = nums.length - 1;
        while (l < r) {
            if (nums[l] + nums[r] == k) {
                op++;
                l++;
                r--;
            } else if (nums[l] + nums[r] > k) {
                r--;
            } else {
                l++;
            }
        }
        return op;
    }

}
