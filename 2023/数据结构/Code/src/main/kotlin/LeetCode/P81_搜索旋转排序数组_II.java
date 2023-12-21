package LeetCode;

public class P81_搜索旋转排序数组_II {
    public boolean search(int[] nums, int target) {
        // 一: 先找到旋转点
        int s = 0;
        for (int i = 0; i < nums.length - 1; i++) {
            if (nums[i] > nums[i + 1]) {
                s = i + 1; // [1,0,1,1,1] // s = 1
                break;
            }
        }
        // 二: 判断左右区间
        int l = 0;
        int r = nums.length - 1;
        int m;
        if (nums[s] <= target && target <= nums[nums.length - 1]) {
            l = s; // 区间[s,nums.length-1];
        } else if (s > 0 && nums[0] <= target && target <= nums[s - 1]) {
            r = s; //[0, s]
        } else {
            return false; // 不在两个区间，直接过滤
        }
        while (l <= r) { // !!!一定需要<=，因为r是有效位置
            m = l + (r - l) / 2;
            if (nums[m] < target) {
                l = m + 1;
            } else if (nums[m] > target) {
                r = m - 1;
            } else {
                return true;
            }
        }
        return false;
    }

}
