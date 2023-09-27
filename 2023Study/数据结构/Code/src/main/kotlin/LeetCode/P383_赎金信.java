package LeetCode;

public class P383_赎金信 {
    public boolean canConstruct(String ransomNote, String magazine) {
        int[] m = new int[26];
        char[] ma = magazine.toCharArray();
        for (char c : ma) {
            m[c - 'a']++; // 统计数量
        }
        for (int i = 0; i < ransomNote.length(); i++) {
            char c = ransomNote.charAt(i);
            if (m[c - 'a'] > 0) {
                m[c - 'a']--;
            } else {
                return false;
            }
        }
        return true;
    }

}
