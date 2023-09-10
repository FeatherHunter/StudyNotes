package LeetCode;

public class P11_盛最多水的容器 {
    /**
     * 思路：l从左，r从右，谁小，谁就移动。遇到大的，就加入
     */
    public int maxArea(int[] height) {
        int max = 0;
        int n = height.length;
        for (int l = 0, r = n - 1; l<r;){
            int total = Math.min(height[l], height[r]) * (r - l);
            if(total > max){
                max = total;
            }
            if(height[l] < height[r]){
                l++;
            }else{
                r--;
            }
        }
        return max;
    }
}
