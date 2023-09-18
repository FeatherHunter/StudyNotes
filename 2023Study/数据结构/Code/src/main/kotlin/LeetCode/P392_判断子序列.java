package LeetCode;

/**
 * https://leetcode.cn/problems/is-subsequence
 * 0ms 击败 100.00%使用 Java 的用户
 */
public class P392_判断子序列 {
    public boolean isSubsequence(String s, String t) {
        if (s.length() > t.length()) return false;
        char[] sa = s.toCharArray();
        char[] ta = t.toCharArray();
        int b = 0;
        for (int i = 0; i < sa.length; i++) {
            while (b < ta.length && sa[i] != ta[b]){
                b++; // 找到一样的
            }
            if(b == ta.length){
                return false; // 找到末尾都没找到搞笑吧
            }
            b++;
        }
        return true;
    }
}
