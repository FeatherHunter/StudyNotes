package LeetCode;

public class P239_滑动窗口最大值 {

    static int[] queue = new int[100001];
    static int h = 0;
    static int t = 0;

    public static int[] maxSlidingWindow(int[] nums, int k) {
        h = 0;
        t = 0;
        int n = nums.length;
        // 构造k-1的单调队列
        for (int i = 0; i < k - 1; i++) {
            while (h < t && (nums[queue[t - 1]] <= nums[i])){
                t--;
            }
            queue[t++] = i;
        }
        int m = n - k + 1;
        int[] ans = new int[m];
        for (int l = 0, r = k - 1; l < m; l++, r++) {
            while (h < t && (nums[queue[t - 1]] <= nums[r])){
                t--;
            }
            queue[t++] = r;
            ans[l] = nums[queue[h]]; // 头就是最大值
            //
            if(queue[h] == l){
                // 此时整个窗口开始移动。如果最大值的下标为刚刚出去的l，需要移除头
                h++;
            }
        }
        return ans;
    }
}
