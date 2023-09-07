package LeetCode;

/**
 * 给定一个含有 n 个正整数的数组和一个正整数 target 。
 * 找出该数组中满足其总和大于等于 target 的长度最小的连续子数组 ，并返回其长度。
 * 如果不存在符合条件的子数组，返回 0 。
 * https://leetcode.cn/problems/minimum-size-subarray-sum/
 * 🥇
 * 时间 1ms 击败 99.73%使用 Java 的用户
 * 内存 51.28MB 击败 55.68%使用 Java 的用户
 */
public class P203_minimum_size_subarray_sum {

    public static int minSubArrayLen2(int target, int[] nums) {

        if (nums == null || nums.length == 0){
            return 0;
        }
        int min = Integer.MAX_VALUE; // 最小长度
        for(int l = 0, r = 0, sum = 0; r < nums.length; r++){
            sum += nums[r];
            while (sum - nums[l] >= target){
                sum -= nums[l++];
            }
            if(sum >= target){
                min = Math.min(min, r-l+1);
            }
        }
        return (min == Integer.MAX_VALUE) ? 0 : min;
    }

    public static int minSubArrayLen(int target, int[] nums) {

        if (nums == null || nums.length == 0){
            return 0;
        }
        int l = 0;
        int r = 0;
        int min = Integer.MAX_VALUE; // 最小长度
        int sum = 0;
        while (l < nums.length){
            while (sum < target && r < nums.length){
                sum += nums[r];
                r++;
            }
            if(sum >= target){ // 总数 >= target
                min = Math.min(min, r-l);
            }
            // 找到还是没找到都需要移动左侧边界
            sum -= nums[l];
            l++;
        }
        if(min == Integer.MAX_VALUE){
            min = 0;
        }
        return min;
    }
}
