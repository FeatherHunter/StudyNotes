package LeetCode;


/**
 * 922. 按奇偶排序数组 II
 * https://leetcode.cn/problems/sort-array-by-parity-ii/
 * 解题思路：
 *  1. 双指针
 */
public class P922_sort_array_by_parity_ii {

    /**
     * 版本二，差不多
     */
    public static int[] sortArrayByParityII2(int[] nums) {
        int n = nums.length;
        for (int even=0,odd=1;even<n && odd<n;){
            if((nums[n-1]&1) == 1){
                // 最后一位是奇数
                swap(nums, odd, n-1);
                odd+=2;
            }else{
                // 最后一位是偶数
                swap(nums, even, n-1);
                even+=2;
            }
        }
        return nums;
    }

    /**
     * 奇数指针、偶数指针
     */
    public static int[] sortArrayByParityII(int[] nums) {

        if(nums == null) return null;
        for (int i=0,j=1;i<nums.length && j<nums.length;i+=2,j+=2){
            if(nums[i] % 2 == 1 && nums[j] % 2 == 0){
                swap(nums, i, j);
                continue;
            }
            if(nums[i] % 2 == 1){
                i-=2;
            }else if (nums[j] % 2 == 0){
                j-=2;
            }
        }
        return nums;
    }
    public static void swap(int[] nums, int i, int j){
        if (i == j) return;
        nums[i] = nums[i] ^ nums[j];
        nums[j] = nums[i] ^ nums[j];
        nums[i] = nums[i] ^ nums[j];
    }
}
