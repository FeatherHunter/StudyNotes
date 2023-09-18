package LeetCode;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 给你一个整数数组 nums ，按要求返回一个新数组 counts 。数组 counts 有该性质： counts[i] 的值是  nums[i] 右侧小于 nums[i] 的元素的数量。
 */

public class P315_计算右侧小于当前元素的个数 {

    static int[] help = new int[500001];
    static int[] res = new int[500001]; // 存放结果

    /**
     * 思路分析，右边东西的数量，等于左半区域，右半区域
     * [5,2,6,1] [2,1,1,0]
     * 分为区域 [5] [2] [6] [1]
     * [5,2] => [1,0,0,0] => [2,5]
     * [6,1] => [1,0,1,0] => [1,6]
     * [2,5]+[1,6] 第一轮 [1,1,1,0]
     * 第二轮 5比较6（默认有1的那个次数了） [2,1,1,0]
     * 结束哦
     */
    public static List<Integer> countSmaller(int[] nums) {
        // 切记一定需要还原static数据
        Arrays.fill(res, 0);
        Arrays.fill(help, 0);

        List<Integer> ans = new ArrayList<Integer>();
        if(nums == null) return ans;
        // 注意点：要记得归并分治，会改变数组内容
        int[] indexs = new int[nums.length];
        for (int i = 0; i < nums.length; i++) {
            indexs[i] = i; // 对索引数组排序
        }
        counts(nums, indexs, 0, nums.length - 1);

        // 遍历时会有改变，因此需要复制
        for (int i = 0; i < nums.length; i++) {
            ans.add(res[i]); // 下标找到结果哦
        }
        return ans;
    }
    // 索引数组归并
    public static void counts(int[] nums, int[] arr, int l, int r){
        if(l >= r) return;
        int m = l + (r-l)/2;
        counts(nums, arr, l, m);
        counts(nums, arr, m+1, r);
        // 升级版本二:
        if(nums[arr[m]] <= nums[arr[m+1]]){
            return;
        }
        merge(nums, arr, l, m, r);
    }

    public static void merge(int[] nums, int[] arr, int l, int m, int r){
        // 1、计算统计出数据
        for (int i = l, j = m + 1, sum = 0; i <= m; i++){
            while (j <= r && nums[arr[i]] > nums[arr[j]]){ // 下标对应数据的比较
                sum++;
                j++;
            }
            res[arr[i]] += sum; // 下标处的结果++哦
        }


        // 2、数组合并
        int i = l;
        int a = l;
        int b = m + 1;
        while (a <= m && b <= r){ // 下标对应的数据比较
            help[i++] = (nums[arr[a]] <= nums[arr[b]]) ? arr[a++] : arr[b++];
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
    }

}
