package LeetCode;

import java.util.ArrayList;
import java.util.List;

public class P228_汇总区间 {
    public List<String> summaryRanges(int[] nums) {

        List<String> ans = new ArrayList<>();
        if(nums.length == 0) return ans;
        int start = nums[0];
        int end = nums[0];
        for (int i = 1; i < nums.length; i++) {
            if(nums[i] == nums[i-1] + 1){
                // 符合区间
                end = nums[i];
            }else{
                // 不符合要求,需要增加结果
                ans.add((start == end) ? String.valueOf(start) : start + "->" + end);
                start = nums[i];
                end = nums[i]; // 开始和结束都从当前数值开始
            }
        }
        // 增加到结尾处，最后没有加入的内容
        ans.add((start == end) ? String.valueOf(start) : start + "->" + end);
        return ans;
    }
}
