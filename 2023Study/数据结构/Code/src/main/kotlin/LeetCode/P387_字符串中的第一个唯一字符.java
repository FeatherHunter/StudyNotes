package LeetCode;

public class P387_字符串中的第一个唯一字符 {
    public int firstUniqChar(String so) {
        int[] m = new int[26];
        char[] s = so.toCharArray();
        for (int i = 0; i < s.length; i++) {
            m[s[i] - 'a']++; // 统计数量
        }
        for (int i = 0; i < s.length; i++) {
            if (m[s[i] - 'a'] == 1) {
                return i;
            }
        }
        return -1;
    }

}
