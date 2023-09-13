package LeetCode;

/**
 * 给你一个整数数组 nums ，除某个元素仅出现 一次 外，其余每个元素都恰出现 三次 。请你找出并返回那个只出现了一次的元素。
 */
public class P137_只出现一次的数字II {
    public int singleNumber(int[] nums) {
        int[] bits = new int[32]; // 32位
        for (int num : nums) {
            for (int i = 0; i < 32; i++) {
                bits[i] += (num >> i) & 1;
            }
        }
        int ans = 0;
        for (int i = 0; i < bits.length; i++) {
            ans |= bits[i] % 3 << i;
        }
        return ans;
    }
}
