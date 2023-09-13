package LeetCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * https://leetcode.cn/problems/pascals-triangle-ii/
 * 0ms 击败 100.00%使用 Java 的用户
 * 38.18MB 击败 59.69%使用 Java 的用户
 */
public class P119_杨辉三角II {
    public List<Integer> getRow(int rowIndex) {
        int[] help = new int[rowIndex + 1];
        List<Integer> ans = new ArrayList<>();
        for (int i = 0; i <= rowIndex; i++) {
            for (int j = i; j >= 0; j--) {
                if(j == 0 || j == i){
                    help[j] = 1;
                }else{
                    help[j] = help[j] + help[j - 1];
                }
                if(i == rowIndex){
                    ans.add(help[j]);
                }
            }
        }

        return ans;
    }
}
