package LeetCode;

import java.util.HashSet;

public class P467_环绕字符串中唯一的子字符串 {
    /**
     * s = "zab"
     * ("z", "a", "b", "za", "ab", and "zab")
     * //     * dp[0~2]
     * //     * dp[i]以i字符结尾的匹配到的子串数量
     * //     * dp[0] = 'z'
     * //     * dp[1] = 'a' 'za'
     * //     * dp[2] = 'b' 'ab' 'zab'
     * //     *
     * //     * 没有注意到什么？
     * //     *  1. 需要去重
     * //     *  2. 需要慎重考虑场景，关于特定情况，要列好
     * //     *  3. 没有考虑到内部的for循环是s参数，开始的
     * dp[0] = dp['a'] = 以a结尾的最大长度
     * dp[25] = dp['z'] = 以z结尾的最大长度
     * <p>
     * 最后累加，每个都是最大值(去重)，排除了重复情况
     * 💯2ms击败100.00%使用 Java 的用户
     * 💯42.25MB击败86.03%使用 Java 的用户
     */
    public int findSubstringInWraproundString(String s) {
        char[] w = s.toCharArray();
        int dp[] = new int[26];

//        for (int i = 0; i < w.length; i++) {
//            int start = i-1;
//            while (start >= 0 && (w[start] == w[start + 1] - 1 || (w[start] == 'z' && w[start + 1] == 'a'))){
//                start--;
//            }
//            int length = i - start;
//            dp[w[i] - 'a'] = Math.max(length, dp[w[i] - 'a']); // 最大值去重
//        }
        /**
         * 特殊场景：abcdefghijklmnopqrstuvwxyz无限循环
         * 从底部开始往前动态规划，前面的只会更短小。然后一次性跳过这些区域
         */
        for (int i = w.length - 1; i >= 0;) {
            int start = i - 1;
            while (start >= 0 && (w[start] == w[start + 1] - 1 || (w[start] == 'z' && w[start + 1] == 'a'))) {
                start--;
            }
            int length = i - start;
            dp[w[i] - 'a'] = Math.max(length, dp[w[i] - 'a']); // 最大值去重
            for (int j = i - 1; j > start; j--) {
                length--;
                dp[w[j] - 'a'] = Math.max(length, dp[w[j] - 'a']); // 最大值去重
            }
            i = start;
        }

        int total = 0;
        for (int i = 0; i < dp.length; i++) {
            total += dp[i]; // 累加
        }
        return total;
    }
}
