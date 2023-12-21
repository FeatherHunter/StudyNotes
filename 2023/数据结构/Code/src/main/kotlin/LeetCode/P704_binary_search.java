package LeetCode;

/**
 * 704. 二分查找
 * https://leetcode.cn/problems/binary-search/
 */
public class P704_binary_search {
    public static int search(int[] nums, int target) {
        int l = 0;
        int r = nums.length-1;
        int mid;
        while (l <= r){
            mid = l + ((r-l) >> 1);
            if(nums[mid] == target){
                return mid;
            }else if (nums[mid] < target){
                l = mid + 1;
            }else{
                r = mid - 1;
            }
        }
        return -1;
    }
}
