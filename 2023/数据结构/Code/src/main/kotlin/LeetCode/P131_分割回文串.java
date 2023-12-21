package LeetCode;

import java.util.ArrayList;
import java.util.List;

public class P131_分割回文串 {
    public List<List<String>> partition(String s) {
        List<List<String>> ans = new ArrayList<>();
        dfs(s, ans, new ArrayList<>());
        return ans;
    }

    public void dfs(String s, List<List<String>> ans, List<String> res) {
        // String为空, 结束
        if (s.isEmpty()) {
            ans.add(new ArrayList<>(res));
            return;
        }
        int n = s.length();
        for (int i = 1; i <= n; i++) {
            String left = s.substring(0, i);
            // 不合法，整个链路都结束哦
            if (isReverse(left) == false) {
                continue;
            }
            res.add(left);
            dfs(s.substring(i), ans, res);
            res.remove(res.size() - 1); // 还原
        }
    }

    public boolean isReverse(String s) {
        char[] w = s.toCharArray();
        int l = 0;
        int r = w.length - 1;
        while (l < w.length) {
            if (w[l] != w[r]) {
                return false;
            }
            l++;
            r--;
        }
        return true;
    }

}
