package LeetCode;

public class P34_在排序数组中查找元素的第一个和最后一个位置_二分法 {
    public int[] searchRange(int[] nums, int target) {
        int[] res = new int[]{-1, -1};
        int l = 0;
        int r = nums.length - 1; // 因此需要l<=r
        while (l <= r) { //
            int m = l + (r - l) / 2;
            if (nums[m] > target) {
                r = m - 1;
            } else if (nums[m] < target) {
                l = m + 1;
            } else {
                // 二分找到目标
                l = r = m;
                while (l >= 0 && nums[l] == target) {
                    l--;
                }
                res[0] = l + 1; // 左边
                while (r < nums.length && nums[r] == target) {
                    r++;
                }
                res[1] = r - 1; // 右边
                break;
            }
        }
        return res;
    }

}
