package LeetCode;

import java.util.HashMap;

public class P409_最长回文串 {
    public int longestPalindrome(String s) {
        int[] map = new int[52];
        char[] w = s.toCharArray();
        int ans = 0;
        for (char c : w) {
            int count = 0;
            if(c >= 'a' && c <= 'z'){
                count = map[c - 'a'];
            }else{
                count = map[26 + (c - 'A')];
            }
            if(count == 0){
                count++;
            }else if (count == 1){
                ans+=2;
                count = 0;
            }
            if(c >= 'a' && c <= 'z'){
                map[c - 'a'] = count;
            }else{
                map[26 + (c - 'A')] = count;
            }
        }
        return (w.length > ans)? ans + 1 : ans;
    }
}
