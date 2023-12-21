package LeetCode;


/**
 * 287. 寻找重复数
 *     https://leetcode.cn/problems/find-the-duplicate-number/
 *   1. 双指针
 */
public class P287_find_the_duplicate_number {
    public static int findDuplicate(int[] nums) {

        int slow = nums[0];
        int fast = nums[nums[0]];
        while (slow != fast){
            slow = nums[slow];
            fast = nums[nums[fast]];
        }
        fast=0;
        while (slow != fast){
            slow = nums[slow];
            fast = nums[fast];
        }
        return slow;
    }
}
