package LeetCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class P90_子集_II {
    List<Integer> mPath = new ArrayList<>();
    List<List<Integer>> mAns = new ArrayList<>();

    public List<List<Integer>> subsetsWithDup(int[] nums) {
        Arrays.sort(nums);
        dfs(0, nums);
        return mAns;
    }

    // 下标i看是否要加入路径
    public void dfs(int i, int[] nums) {
        if (i == nums.length) {
            mAns.add(new ArrayList<>(mPath));
            return;
        }
        // 选择当前数字
        mPath.add(nums[i]);
        dfs(i + 1, nums);
        mPath.remove(mPath.size() - 1);
        // 不选择当前数字，一样的跳过
        while (i + 1 < nums.length && nums[i + 1] == nums[i]) {
            i++;
        }
        dfs(i + 1, nums);
    }

}
