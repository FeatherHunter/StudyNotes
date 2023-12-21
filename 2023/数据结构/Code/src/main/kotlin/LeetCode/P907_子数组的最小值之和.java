package LeetCode;

public class P907_子数组的最小值之和 {
    public int sumSubarrayMins(int[] arr) {
        long ans = 0;
        int n = arr.length;
        int[] stack = new int[n];
        int index = -1;
        for (int i = 0; i < n; i++) {
            while (index != -1 && (arr[stack[index]] >= arr[i])){
                // 栈比我们的大，亏贼
                int cur = stack[index--];
                int left = (index == -1) ? -1 : stack[index];// 存在左侧的
                ans = (ans + (long)(cur - left) * (i - cur) * arr[cur]) % 1000000007;
            }
            stack[++index] = i;
        }
        while (index != -1){
            int cur = stack[index--];
            int left = (index == -1) ? -1 : stack[index];// 存在左侧的
            ans = (ans + (long)(cur - left) * (arr.length - cur) * arr[cur]) % 1000000007; // 从右到左了。
        }
        return (int) ans;
    }
}
