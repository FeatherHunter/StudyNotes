package LeetCode;

import java.util.Stack;

/**
 * https://leetcode.cn/problems/count-of-range-sums/
 *
 */
public class P327_区间和的个数 {
    /**
     * nums = [5,-2,1,-1], lower = -2, upper = 3
     * 前缀和进行归并，前缀和只要排序，一定是不改变区间和的数量的。
     * [5,3,4,3]
     *
     * 前缀和+归并思路：跨区域，右侧减去左侧的前缀和，得到区间和，符合条件的 + 1
     */
    static int[] help = new int[100001];
    public static int countRangeSum(int[] nums, int lower, int upper) {

        if (nums == null && nums.length == 0){
            return -1;
        }
        int n = nums.length;
        long[] sums = new long[n]; // 前缀和数组
        sums[0] = nums[0];
        for (int i = 1; i < nums.length; i++) {
            sums[i] = sums[i - 1] + nums[i];
        }
        int[] indexs = new int[n];
        for (int i = 0; i < indexs.length; i++) {
            indexs[i] = i;
        }
        return counts(sums, 0, n - 1, indexs, lower, upper);
    }
    
    public static int counts(long[] sums, int l, int r, int[] indexs, int lower, int upper){
        if (l >= r) return (sums[l] <= upper && sums[l] >= lower) ? 1 : 0;
        int m = l + (r-l)/2; // 需要注意r-l
        return counts(sums, l, m, indexs, lower, upper) + counts(sums, m+1, r, indexs, lower, upper) + merge(sums, l, m, r, indexs, lower, upper);
    }
    public static int merge(long[] sums, int l, int m, int r, int[] indexs, int lower, int upper){
        // 1. 计算出结果哦
        // [3,5，6][3,4，7] // 以左侧开头3为例子。 在【3，4，7】中找到正好符合 >= lower的l处，找到符合<= upper的最大处。当3->5后，lower的l处
        int ans = 0;
        for (int i = l, j = m + 1, k = m + 1; i <= m; i++) {
            while (j <= r && (sums[indexs[j]] - sums[indexs[i]]) < lower){
                j++; //找到正好 >= lower的地方
            }
            while (k <= r && (sums[indexs[k]] - sums[indexs[i]]) <= upper){
                k++; // 找到正好> upper的地方
            }
            // 3，4，5，6下标，j = 0, k = 3.数量就是k-j
            ans += k - j;
        }

        // 2. 归并哦
        int i = l;
        int a = l;
        int b = m + 1;
        while (a <= m && b <= r){
            help[i++] = (sums[indexs[a]] <= sums[indexs[b]]) ? indexs[a++] : indexs[b++];
        }
        while (a <= m){
            help[i++] = indexs[a++];
        }
        while (b <= r){
            help[i++] = indexs[b++];
        }
        for (i = l; i <= r; i++){
            indexs[i] = help[i];
        }

        return ans;
    }
}
