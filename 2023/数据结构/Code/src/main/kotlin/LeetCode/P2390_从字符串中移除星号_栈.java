package LeetCode;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * https://leetcode.cn/problems/removing-stars-from-a-string
 * 13ms 击败 95.07%使用 Java 的用户
 */
public class P2390_从字符串中移除星号_栈 {

    static char[] stack = new char[100001];
    static int top = 0;
    public String removeStars(String s) {
        top = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if(c == '*'){
                if(top > 0){
                    top--;
                }
            }else{
                stack[top++] = c;
            }
        }
//        StringBuilder b = new StringBuilder();
//        while (top > 0){
//            b.insert(0, stack[--top]);
//        }
        return new String(stack, 0, top);
    }
}
