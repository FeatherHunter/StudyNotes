package LeetCode;

/**
 * https://leetcode.cn/problems/first-missing-positive/
 * 1ms 击败 97.80%使用 Java 的用户
 */
public class P41_缺失的第一个正数 {
    public static int firstMissingPositive(int[] nums) {
        for (int l = 0, r = nums.length; l < r; l++) {
            while (nums[l] != l + 1 && nums[l] > 0 && nums[l] < r && nums[nums[l] - 1] != nums[l]){
                swap(nums, l, nums[l] - 1); // 符合条件的无限交换。
            }
        }
        for (int i = 0; i < nums.length; i++) {
            if(nums[i] != i + 1){
                return i + 1;
            }
        }
        return nums.length + 1;
    }
    public static void swap(int[] nums, int i, int j){
        if(i == j) return;
        nums[i] = nums[i] ^ nums[j];
        nums[j] = nums[i] ^ nums[j];
        nums[i] = nums[i] ^ nums[j];
    }
}
