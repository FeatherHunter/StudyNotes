package LeetCode;

import java.util.List;

public class P120_三角形最小路径和 {
    // 2ms 击败 96.05%使用 Java 的用户
    public int minimumTotal(List<List<Integer>> triangle) {
        int N = triangle.size(); // dp数组大小
        int[] dp = new int[N];
        dp[0] = triangle.get(0).get(0); // 第一行
        for (int i = 1; i < triangle.size(); i++) {
            List<Integer> list = triangle.get(i);
            // 1、末尾
            dp[list.size() - 1] = dp[list.size() - 2] + list.get(list.size() - 1);
            // 2、dp[j]来源于dp[j-1]和dp[j]最小值 + 当前值
            for (int j = list.size() - 2; j > 0; j--) {
                Integer num = list.get(j);
                dp[j] = Math.min(dp[j - 1], dp[j]) + num; // 路径和
            }
            // 3、开头数据来源于上层
            dp[0] = dp[0] + list.get(0);
        }
        int min = Integer.MAX_VALUE;
        for (int num : dp) {
            if (num < min) {
                min = num;
            }
        }
        return min;
    }

}
