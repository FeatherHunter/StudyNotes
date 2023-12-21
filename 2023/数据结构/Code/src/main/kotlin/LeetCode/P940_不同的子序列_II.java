package LeetCode;

import java.util.ArrayList;
import java.util.HashSet;

public class P940_不同的子序列_II {
    /**
     * src 存在 c存在，不用加了
     * <p>
     * 版本一：正确，但超时
     * * 输入：s = "abaab"
     * * All: 考虑空集,多少子序列 = 1
     * * dp[a-z]
     * * 空{} 1
     * 版本二：规则：all=总数，每次新增内容 = all-上次当前字符新增。all = all + 新增
     */
    public int distinctSubseqIIold(String s) {
        ArrayList<String> list = new ArrayList<>();
        HashSet<String> set = new HashSet<>();
        list.add("");
        set.add("");
        int all = 1;
        char w[] = s.toCharArray();
        for (char c : w) {
            int size = list.size();
            for (int i = size - 1; i >= 0; i--) {
                String t = list.get(i) + c;
// 凭借结果没有加入过
                if (!set.contains(t)) {
                    list.add(t);
                    set.add(t); // 添加了目标哦
                    all++;
                }
            }
        }
        return all - 1;
    }

    public static final int MOD = 1000000007;

    // 规则：all=总数，每次新增内容 = all-上次当前字符新增。all = all + 新增
// 记住要按照规则，写代码
// 除法取模 a - b = (a - b + MOD)%MOD
    public int distinctSubseqII(String s) {
        long all = 1; // 注意越界问题
        char w[] = s.toCharArray();
        long[] dp = new long[26];
        for (char c : w) {
            long add = (all - dp[c - 'a'] + MOD) % MOD;
            dp[c - 'a'] = (dp[c - 'a'] + add) % MOD;
            all = (all + add) % MOD;
        }
        return (int) (all - 1 + MOD) % MOD;
    }

}
