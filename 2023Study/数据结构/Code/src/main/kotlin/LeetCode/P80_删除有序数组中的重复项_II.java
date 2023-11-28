package LeetCode;

public class P80_删除有序数组中的重复项_II {
    // 双指针
    public int removeDuplicates(int[] nums) {
        int l = 1;
        int r = 1;
        int count = 1;
        int last = nums[0];
        while (r < nums.length) {
            if (last == nums[r]) {
                count++;
            } else {
                count = 1; // 当前字符和上一个不一样，重新计数为1
            }
            last = nums[r]; // 注意该细节
            if (count <= 2) { // 2个字符以内的都放到左侧
                nums[l] = nums[r];
                l++;
            }
            r++;
        }
        return l;
    }

}
