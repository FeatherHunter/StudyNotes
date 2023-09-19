package LeetCode;

/**
 * https://leetcode.cn/problems/find-pivot-index
 *
 */
public class P724_寻找数组的中心下标_前缀和 {

    public int pivotIndex(int[] nums) {

        int[] sum = new int[nums.length];
        sum[0] = nums[0];
        for (int i = 1; i < sum.length; i++) {
            sum[i] = sum[i-1] + nums[i];
        }
        for (int i = 0; i < sum.length; i++) {
            int l = 0;
            if (i > 0){
                l = sum[i-1];
            }
            int r = sum[sum.length-1]-sum[i];
            if(l == r){
                return i;
            }
        }
        return -1;//没找到
    }
}
