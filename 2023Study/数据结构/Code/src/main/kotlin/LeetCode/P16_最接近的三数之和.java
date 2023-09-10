package LeetCode;

import java.util.Arrays;

public class P16_最接近的三数之和 {
    // O（n^2）
    public int threeSumClosest(int[] nums, int target) {
        Arrays.sort(nums);
        int best = Integer.MAX_VALUE;
        int n = nums.length;
        for (int i = 0; i < n; i++) {
            if(i > 0 && nums[i] == nums[i-1]){
                continue;
            }
            int j = i + 1;
            int k = n - 1;
            while (j < k){
                int sum = nums[i] + nums[j] + nums[k];
                if(Math.abs(sum - target) < Math.abs(best - target)){
                    best = sum;
                }
                if(sum > target){
                    k--;
                }else{
                    j++;
                }
            }
        }

        return best;
    }
}
