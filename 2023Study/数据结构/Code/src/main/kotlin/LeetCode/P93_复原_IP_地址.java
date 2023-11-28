package LeetCode;

import java.util.ArrayList;
import java.util.List;

public class P93_复原_IP_地址 {
    public List<String> restoreIpAddresses(String s) {
        List<String> ans = new ArrayList<>();
        dfs(s, 3, ans, "");
        return ans;
    }

    public void dfs(String s, int rest, List<String> ans, String path) {
        if (s == null || s.length() == 0) {
            return; // 不合法
        }
        if (rest == 0) {
            if ((s.length() > 1 && s.charAt(0) == '0') || s.length() > 3) {
                return;
            }
            Integer num = Integer.valueOf(s);
            if (num > 255) {
                return;
            } else {
                ans.add(path + s);
            }
        }
        if (s.length() > 0) {
            dfs(s.substring(1), rest - 1, ans, path + s.substring(0, 1) + ".");
        }
        if (s.length() > 1 && s.charAt(0) != '0') {
            dfs(s.substring(2), rest - 1, ans, path + s.substring(0, 2) + ".");
        }
        if (s.length() > 2 && s.charAt(0) != '0' && Integer.valueOf(s.substring(0, 3)) <= 255) { // 符合要求
            dfs(s.substring(3), rest - 1, ans, path + s.substring(0, 3) + ".");
        }
    }

}
