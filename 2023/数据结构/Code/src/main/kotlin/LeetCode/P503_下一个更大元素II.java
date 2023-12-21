package LeetCode;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

public class P503_下一个更大元素II {

    /**
     * 数组实现栈:
     * 2ms 击败 99.49%使用 Java 的用户
     */
    public int[] nextGreaterElements1(int[] nums) {
        int n =  nums.length;
        int[] right = new int[n];
        Arrays.fill(right, -1);
        int[] stack = new int[n*2];
        int index = -1;
        for (int i = 0; i <  n * 2; i++) {
            while ((index != -1) && (nums[stack[index]] < nums[i %  n])){
                int top = stack[index--];
                right[top] = nums[i % n];
            }
            stack[++index] = i % n;
        }
        return right;
    }
    public int[] nextGreaterElements(int[] nums) {
        int n =  nums.length;
        int[] right = new int[n];
        Arrays.fill(right, -1);
        Deque<Integer> stack = new ArrayDeque<>();
        for (int i = 0; i <  n * 2; i++) {
            while (!stack.isEmpty() && (nums[stack.peek()] < nums[i %  n])){
                int top = stack.pop();
                right[top] = nums[i % n];
            }
            stack.push(i % n); // 一样的也出栈
        }
        return right;
    }
}
