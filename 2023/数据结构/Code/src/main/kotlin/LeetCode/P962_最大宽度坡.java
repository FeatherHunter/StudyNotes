package LeetCode;

/**
 * 第一步：进入的可能性。[6,0,8,2,1,5] 下降时才有进栈必要。小 🦆 大，才需要放入。
 * 第二部：单调栈弹出的可能性。从右往左。 <= nums[right]出栈
 * https://leetcode.cn/problems/maximum-width-ramp
 * 2ms 击败 99.37%使用 Java 的用户
 */
public class P962_最大宽度坡 {
    public int maxWidthRamp(int[] nums) {
        int[] stack = new int[nums.length];
        int index = 0;
        for (int i = 1; i < nums.length; i++) {
            if (nums[stack[index]] > nums[i]) {
                stack[++index] = i;
            }
        } // 第一步：小压大，入栈
        int max = 0;
        for (int j = nums.length - 1; j >= 0; j--) {
            while ((index != - 1) && nums[stack[index]] <= nums[j]) {
                max = Math.max(max, j - stack[index--]);// 出栈了
            }
        } // 从右到左，找到小的，出栈
        return max;
    }
}
