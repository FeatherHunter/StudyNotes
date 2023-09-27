package LeetCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class P49_字母异位词分组 {
    /**
     * ================================
     * 思路，将字符串的字符排序，字母异位词一定是一个结果。
     * 用Map根据String来存放列表，放String
     * ======================================
     */
    public List<List<String>> groupAnagrams(String[] strs) {
        HashMap<String, List<String>> map = new HashMap<>();
        for (int i = 0; i < strs.length; i++) {
            char[] s = strs[i].toCharArray();
            Arrays.sort(s); // 排序
            String t = new String(s); // 构造出相同String用作key
            if (!map.containsKey(t)) {
                map.put(t, new ArrayList<>());
            }
            map.get(t).add(strs[i]); // 将原始String加入到列表中
        }
        List<List<String>> ans = new ArrayList<>();
        for (List<String> list : map.values()) {
            ans.add(list);
        }
        return ans;
    }

}
