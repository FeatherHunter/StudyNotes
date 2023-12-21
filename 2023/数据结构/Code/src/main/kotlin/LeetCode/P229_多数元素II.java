package LeetCode;

import java.util.ArrayList;
import java.util.List;

public class P229_多数元素II {
    public List<Integer> majorityElement(int[] nums) {
        List<Integer> ans = new ArrayList<>();
        quickSort(nums, 0, nums.length - 1);
        for (int l = 0, r = (nums.length / 3); r < nums.length; ) {
            if (nums[l] == nums[r]) {
                ans.add(nums[l]);
                while (l < nums.length && nums[l] == nums[r]) {
                    l++;
                }
                r = l + (nums.length / 3);
            } else {
                l++;
                r++;
            }
        }
        return ans;
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
