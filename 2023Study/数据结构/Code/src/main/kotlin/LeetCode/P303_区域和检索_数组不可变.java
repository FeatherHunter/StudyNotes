package LeetCode;

public class P303_区域和检索_数组不可变 {
    // 前缀和 7ms 击败 100.00%使用 Java 的用户
    class NumArray {
        int[] sum;

        public NumArray(int[] nums) {
            sum = new int[nums.length];
            sum[0] = nums[0];
            for (int i = 1; i < nums.length; i++) {
                sum[i] = sum[i - 1] + nums[i];
            }
        }

        public int sumRange(int left, int right) {
            int lsum = (left > 0) ? sum[left - 1] : 0;
            int rsum = (right >= sum.length) ? sum[sum.length - 1] : sum[right];
            return rsum - lsum;
        }
    }

}
