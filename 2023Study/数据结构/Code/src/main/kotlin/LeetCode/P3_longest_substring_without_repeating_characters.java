package LeetCode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/**
 * 3. 无重复字符的最长子串:
 *  https://leetcode.cn/problems/longest-substring-without-repeating-characters/
 * LCR 016. 无重复字符的最长子串 : https://leetcode.cn/problems/wtcaE1/
 **/
public class P3_longest_substring_without_repeating_characters {

    /**
     * 2ms 击败 96.81%使用 Java 的用户
     */
    public static int lengthOfLongestSubstring2(String s) {

        char[] arr = s.toCharArray();
        int n = arr.length;
        int[] last = new int[256]; // char的范围是0~255
        Arrays.fill(last, - 1);
        int max = 0;
        for(int l=0,r=0;r<n;r++){
            l = Math.max(l, last[arr[r]]+1); // l为上一次r出现的位置+1
            max = Math.max(max, r - l + 1);
            last[arr[r]] = r; // 记录r当前字符，出现的位置是r
        }
        return max;
    }

    /**
     *  时间 5ms 击败 68.94%使用 Java 的用户
     */
    public static int lengthOfLongestSubstring(String s) {

        HashMap<Integer,Integer> map = new HashMap<>();
        char[] arr = s.toCharArray();
        int max = 0;
        for(int l=0,r=0;r<arr.length;r++){
            if(!map.containsKey((int) arr[r])){
                map.put((int) arr[r], r); // 记录下表
                if(r == arr.length-1){
                    // 最后一次了
                    max = Math.max(max, r-l+1);
                }
                continue;
            }
            max = Math.max(max, r-l);
            // 无法添加，说明重复了
            // 需要找到该重复的位置的后面重来走

            int index = map.get((int) arr[r]);
            while (l <= index){
                map.remove((int) arr[l++]);
            }
            // 此时清除干净了
            r--;
        }
        return max;
    }
}
