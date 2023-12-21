package LeetCode;

public class P1004_最大连续1的个数III {
    public int longestOnes(int[] nums, int k) {
        int l = 0;
        int r = 0;
        int max = 0;
        while (r < nums.length) {
            if (nums[r++] == 0) {
                k--;
            }
            while (k < 0) {
                if (nums[l++] == 0) {
                    k++;
                }
            }
            max = Math.max(max, r - l);
        }
        return max;
    }

}
