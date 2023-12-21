package LeetCode;

import java.util.Arrays;

/**
 * 第一步：词频
 * 第二部：栈，大🦆小，根据词频判断是否可以出栈，
 * "cbacdcbc"
 * a:1 b:1 c:2 d:1
 * a c d
 */
public class P316_去除重复字母 {
    public String removeDuplicateLetters(String s) {
        // 1、词频
        int[] map = new int[26];
        boolean[] enter = new boolean[26];
        char[] arr = s.toCharArray();
        for (char c : arr) {
            map[c - 'a']++;
        }
        // 2、单调栈，大压小+词频
        int[] stack = new int[s.length()];
        int index = -1;
        for (char c : arr) {
            if(!enter[c - 'a']){
                while ((index != -1) && c < stack[index] && map[stack[index] - 'a'] > 0){
                    enter[stack[index] - 'a'] = false;
                    index--; //出栈
                }
                if(index == -1 || (c != stack[index])){
                    stack[++index] = c; // 可以入栈
                    enter[c - 'a'] = true;
                }
            }//无论是否加入，i--
            map[c - 'a']--; // 词频，处理好哦
        }
        StringBuilder builder = new StringBuilder();
        while (index != -1){
            builder.insert(0, (char)stack[index--]);
        }
        return builder.toString();
    }
}
