package LeetCode;

public class P1493_删掉一个元素以后全为1的最长子数组 {
    // 1004.题目基础山，只翻转1个0，然后去除该0的长度。
    public int longestSubarray(int[] nums) {
        int l = 0;
        int r = 0;
        int k = 1;
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
            max = Math.max(max, r - l - 1); // 有一个0翻转为1，需要去除该1的长度
        }
        return max;
    }

}
