package LeetCode;

/**
 * 42. 接雨水
 * https://leetcode.cn/problems/trapping-rain-water/
 */
public class P42_trapping_rain_water {

    /**
     * 时间复杂度O(n)
     * 空间复杂度O(1)
     * // 利用max只会增加不会减少的单调性
     */
    public static int trap2(int[] height) {
        int n = height.length;
        int lmax = height[0];
        int rmax = height[n-1];

        int ans = 0;
        int l = 1;
        int r = n-2;

        while (l <= r){
            if(lmax < rmax){
                ans += Math.max(0, lmax - height[l]);
                lmax = Math.max(lmax, height[l++]);
            }else{
                ans += Math.max(0, rmax - height[r]);
                rmax = Math.max(rmax, height[r--]);
            }
        }
        return ans;
    }

    /**
     * 时间复杂度O(n)
     * 空间复杂度O(n)
     */
    public static int trap(int[] height) {
        int n = height.length;
        int[] lmax = new int[n];
        int[] rmax = new int[n];
        lmax[0] = height[0];
        for (int i = 1; i < lmax.length; i++) {
            lmax[i] = Math.max(lmax[i-1], height[i]);
        }
        rmax[n-1] = height[n-1];
        for (int i = n-2; i >= 0; i--) {
            rmax[i] = Math.max(rmax[i+1], height[i]);
        }

        int ans = 0;
        for (int i = 1; i < n-1; i++) {
            ans += Math.max(Math.min(lmax[i-1], rmax[i+1]) - height[i], 0);
        }
        return ans;
    }
}
