package LeetCode;

/**
 * https://leetcode.cn/problems/maximum-average-subarray-i
 * 2ms 击败 100.00%使用 Java 的用户
 */
public class P643_子数组最大平均数I_双指针 {
    public double findMaxAverage(int[] nums, int k) {
        int l = 0;
        int r = k;
        int sum = 0;
        for (int i = 0; i < k; i++) {
            sum += nums[i];
        }
        int max = sum;
        while (r < nums.length){
            sum = sum + nums[r] - nums[l];
            max = Math.max(sum, max);
            r++;
            l++;
        }
        return (double) max / k;
    }
}
