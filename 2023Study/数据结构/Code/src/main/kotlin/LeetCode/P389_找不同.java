package LeetCode;

public class P389_找不同 {
    public char findTheDifference(String s, String t) {
        int[] m = new int[26];
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            m[c - 'a']++; // 统计数量
        }
        for (int i = 0; i < t.length(); i++) {
            char c = t.charAt(i);
            if (m[c - 'a'] > 0) {
                m[c - 'a']--;
            } else {
                return c;
            }
        }
        return ' ';
    }

}
