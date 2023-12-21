package LeetCode;

import java.util.ArrayList;
import java.util.List;

/**
 * https://leetcode.cn/problems/pascals-triangle/
 * 1ms 击败 95.94%使用 Java 的用户
 */
public class P118_杨辉三角 {
    public List<List<Integer>> generate(int numRows) {
        List<List<Integer>> ans = new ArrayList<>();
        List<Integer> pre = null;
        for (int i = 0; i < numRows; i++) {

            List<Integer> res = new ArrayList<>();
            for (int j = 0; j < i + 1; j++) {
                if(j == 0 || j == i){
                    res.add(1);
                }else{
                    res.add(pre.get(j - 1) + pre.get(j));
                }
            }
            ans.add(res);
            pre = res;
        }
        return ans;
    }
}
