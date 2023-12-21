package LeetCode;

/**
 * 344. 反转字符串
 * https://leetcode.cn/problems/reverse-string/
 */
public class P344_reverse_string {
    public static void reverseString(char[] s) {
        int l = 0;
        int r = s.length - 1;
        while (l < r){
            swap(s, l++, r--);
        }
    }
    public static void swap(char[] s, int i, int j){
        if (i == j) return;
        s[i] = (char) (s[i] ^ s[j]);
        s[j] = (char) (s[i] ^ s[j]);
        s[i] = (char) (s[i] ^ s[j]);
    }
}
