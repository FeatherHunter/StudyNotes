package LeetCode;

import java.util.ArrayList;
import java.util.List;

public class P77_组合 {
    public List<List<Integer>> combine(int n, int k) {
        List<List<Integer>> ans = new ArrayList<>();
        process(n, 1, new ArrayList<>(), ans, k);
        return ans;
    }

    public void process(int n, int i, List<Integer> res, List<List<Integer>> ans, int k) {
        if (res.size() == k) {
            ans.add(new ArrayList<>(res));
            return;
        }
        while (i <= n) {
            res.add(i);//增加结果
            process(n, i + 1, res, ans, k);
            res.remove(res.size() - 1);// 恢复现场
            i++;
        }
    }

}
