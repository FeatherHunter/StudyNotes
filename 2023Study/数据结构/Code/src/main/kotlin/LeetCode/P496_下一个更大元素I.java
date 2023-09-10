package LeetCode;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

/**
 * https://leetcode.cn/problems/next-greater-element-i/
 */
public class P496_下一个更大元素I {
    public static int[] nextGreaterElement(int[] nums1, int[] nums2) {
        Deque<Integer> stack = new ArrayDeque<>();
        int[] left = new int[nums2.length];
        int[] right = new int[nums2.length];
        int[] map = new int[10001];
        for (int i = 0; i < nums2.length; i++) {
            map[nums2[i]] = i; // 记录下标
            while (!stack.isEmpty() && (nums2[stack.peek()] < nums2[i])){
                int top = stack.pop();
                left[top] = -1; // 默认没有
                if(!stack.isEmpty()){
                    left[top] = stack.peek(); // 有左侧
                }
                right[top] = i;
            }
            // 入栈
            stack.push(i);
        }
        while (!stack.isEmpty()){
            int top = stack.pop();
            left[top] = -1; // 默认没有
            if(!stack.isEmpty()){
                left[top] = stack.peek(); // 有左侧
            }
            right[top] = -1;
        }
        int[] result = new int[nums1.length];
        for (int i = 0; i < nums1.length; i++) {
            int index = right[map[nums1[i]]];
            if(index != -1){
                result[i] = nums2[index];
            }else{
                result[i] = -1;
            }
        }
        return result;
    }
}
