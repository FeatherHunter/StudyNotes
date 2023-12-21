package LeetCode;

public class P169_多数元素 {
    public int majorityElement(int[] nums) {
        quickSort(nums, 0, nums.length - 1);
        return nums[(nums.length - 1) / 2];
    }

    public void quickSort(int[] nums, int l, int r) {
        if (l >= r) {
            return;
        }
        int x = nums[l + (int) (Math.random() * (r - l + 1))]; // r-l+1 代表l~r的长度
        int[] p = partition(nums, l, r, x); // == x的左边界和右边界
        quickSort(nums, l, p[0] - 1);
        quickSort(nums, p[1] + 1, r);
    }

    public int[] partition(int[] nums, int l, int r, int x) {
        int[] res = new int[2];
        for (int i = l; i <= r; ) {
            if (nums[i] < x) {
                swap(nums, i++, l++); // 左边区域扩大，i也需要扩大
            } else if (nums[i] > x) {
                swap(nums, i, r--); // 右边区域缩小
            } else {
                i++; // 一样时，才开始走i
            }
        }
        res[0] = l;
        res[1] = r;
        return res;
    }

    public void swap(int[] nums, int i, int j) {
        if (i == j) return;
        nums[i] = nums[i] ^ nums[j];
        nums[j] = nums[i] ^ nums[j];
        nums[i] = nums[i] ^ nums[j];
    }
}
