package LeetCode;

import java.util.HashMap;

public class P96_不同的二叉搜索树 {
    /**
     *  * 懒缓存，解决n=18以上的超时问题
     *  
     */
    public int numTrees(int n) {
        return numTreesDfs(1, n, new HashMap<>());
    }

    // 从start~end有多少种组成
    public int numTreesDfs(int start, int end, HashMap<Integer, HashMap<Integer, Integer>> map) {
        if (map.containsKey(start)) {
            HashMap<Integer, Integer> inner = map.get(start);
            if (inner.containsKey(end)) {
                return inner.get(end);
            }
        }
        int count = 0;
        if (start > end) {
            count = 1;
        } else {
            for (int i = start; i <= end; i++) {
                int left = numTreesDfs(start, i - 1, map);
                int right = numTreesDfs(i + 1, end, map);
                count += left * right;
            }
        }
        if (!map.containsKey(start)) {
            map.put(start, new HashMap<>());
        }
        HashMap<Integer, Integer> inner = map.get(start);
        inner.put(end, count);//存入结果
        return count;
    }

}
