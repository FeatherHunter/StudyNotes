package LeetCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class P18_四数之和 {
    public List<List<Integer>> fourSum(int[] nums, int target) {
        List<List<Integer>> ans = new ArrayList<>();
        Arrays.sort(nums);
        int n = nums.length;
        for (int i = 0; i < n; i++) {
            if(i > 0 && nums[i] == nums[i - 1]){
                continue;
            }
            for (int j = i + 1; j < n; j++) {
                if(j > i + 1 && nums[j] == nums[j - 1]){
                    continue;
                }
                int k = j + 1;
                int l = n - 1;
                while (k < l){
                    long sum = (long)nums[i] + (long)nums[j] + (long)nums[k] + (long)nums[l];
                    if (sum  < target){
                        k++;
                    }else if(sum > target){
                        l--;
                    }else{
                        if(l == n - 1 || nums[l] != nums[l + 1]){
                            List<Integer> list = new ArrayList<>();
                            list.add(nums[i]);
                            list.add(nums[j]);
                            list.add(nums[k]);
                            list.add(nums[l]);
                            ans.add(list);
                        }
                        l--;
                    }
                }
            }
        }
        return ans;
    }
}
