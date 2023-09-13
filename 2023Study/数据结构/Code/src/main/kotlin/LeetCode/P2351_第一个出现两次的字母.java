package LeetCode;

import java.util.Arrays;

/**
 * https://leetcode.cn/problems/first-letter-to-appear-twice/
 * 0ms 击败 100.00%使用 Java 的用户
 */
public class P2351_第一个出现两次的字母 {
    static int[] map = new int[26];
    public char repeatedCharacter(String s) {
        Arrays.fill(map, 0);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if(map[c - 'a'] > 0){
                return c;
            }else{
                map[c - 'a'] = 1;
            }
        }
        return (char) 0;
    }
}
