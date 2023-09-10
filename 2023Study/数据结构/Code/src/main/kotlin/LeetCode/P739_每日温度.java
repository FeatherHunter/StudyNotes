package LeetCode;

/**
 * https://leetcode.cn/problems/daily-temperatures/
 * 9ms 击败 94.70%使用 Java 的用户
 */
public class P739_每日温度 {
    public static int[] dailyTemperatures(int[] temperatures) {
        int n = temperatures.length;
        int[] stack = new int[n];
        int[] answer = new int[n];

        int index = -1;
        for (int i = 0; i < n; i++) {
            // 相等也加入单调栈
            while (index!=-1 && (temperatures[stack[index]] < temperatures[i])){
                int top = stack[index--];
                answer[top] = i - top;
            }
            stack[++index] = i;// 压入 //
        }
        return answer;
    }
}
