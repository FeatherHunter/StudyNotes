package LeetCode;


import java.util.Arrays;

/**
 * 给定一个数组 nums ，如果 i < j 且 nums[i] > 2*nums[j] 我们就将 (i, j) 称作一个重要翻转对。
 *
 * 你需要返回给定数组中的重要翻转对的数量。
 */
public class P493_翻转对_归并分治 {
    /**
     * 思路是
     * 1. 否可以分为，左区域问题，右区域问题，横跨左右问题
     * 2. 是否可以左右有序后，更容易求出横跨左右的结果
     *
     * 举例：[1,3,2,3,1] 输出2
     * [1,3,2] [3,1] 横跨 3,1
     * [1,2,3] [1,3]
     * 横跨[3,1]
     *
     * 举例二： [2,4,3,5,1]
     * [2,3,4] [1,5]
     * i = 0, j = 3
     * i = 1, j = 3
     * sum = 1; ans = 1;
     *
     */
    static int MAXN = 50001;
    static int[] help = new int[MAXN];
    public int reversePairs(int[] arr) {
        return counts(arr, 0, arr.length - 1);
    }

    public int counts(int[] arr, int l, int r){
        if(l >= r) return 0;
        int m = l + (r - l) / 2;
        return counts(arr, l, m) + counts(arr, m + 1, r) + merge(arr, l, m, r);
    }

    public int merge(int[] arr, int l, int m, int r){
        int ans = 0;
        for (int i = l, j = m + 1, sum = 0; i <= m; i++) {
            while (j <= r && (long)arr[i] > 2 * (long)arr[j]){
                sum++;
                j++;
            }
            ans += sum;
        }

        // 合并流程
        int i = l;
        int a = l;
        int b = m + 1;
        while (a <= m && b <= r){
            help[i++] = (arr[a] <= arr[b]) ? arr[a++] : arr[b++];
        }
        while (a <= m){
            help[i++] = arr[a++];
        }
        while (b <= r){
            help[i++] = arr[b++];
        }
        // 辅助数组数据交给原数组
        for (i = l; i <= r; i++){
            arr[i] = help[i];
        }

        return ans; // 返回答案
    }
}
