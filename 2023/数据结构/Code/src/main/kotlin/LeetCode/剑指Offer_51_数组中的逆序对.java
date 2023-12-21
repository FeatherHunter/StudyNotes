package LeetCode;

/**
 * 在数组中的两个数字，如果前面一个数字大于后面的数字，则这两个数字组成一个逆序对。输入一个数组，求出这个数组中的逆序对的总数。
 */
public class 剑指Offer_51_数组中的逆序对 {
    static int[] help = new int[50001];
    public int reversePairs(int[] nums) {
        if(nums == null) return 0;
        return counts(nums, 0, nums.length - 1);
    }
    public int counts(int[] arr, int l, int r){
        if(l == r) return 0;
        int m = l + (r - l) / 2;
        return counts(arr, l, m) + counts(arr, m + 1, r) + merge(arr, l, m, r);
    }
    public int merge(int[] arr, int l, int m, int r){
        // 1、先计算
        int ans = 0;
        for (int i = l, j = m + 1, sum = 0; i <= m; i++){
            while (j <= r && arr[i] > arr[j]){
                sum++;
                j++;
            }
            ans += sum;
        }

        // 2、合并有序数组
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
        for (i = l; i <= r; i++){
            arr[i] = help[i];
        }
        return ans;
    }
}
