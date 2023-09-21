package LeetCode;

import java.util.LinkedList;

public class P224_基本计算器 {
    public static int calculate(String s) {
        char[] num = s.toCharArray();
        int[] r = cal(num, 0);
        return r[0]; // result
    }

    /**
     * @return r[0]结果 r[1]终止位置
     */
    public static int[] cal(char[] s, int i) {
        LinkedList<String> que = new LinkedList<>();
        int num = 0; // 用于计算数值
        while (i < s.length && s[i] != ')') {
            if (s[i] == ' ') {
                i++;
                continue; // 无视空格
            }
            if (s[i] >= '0' && s[i] <= '9') {
                num = num * 10 + s[i++] - '0';
            } else if (s[i] != '(') { // 遇到运算符
                addNum(que, num); // 将之前的数字，添加进去
                que.addLast(String.valueOf(s[i++])); // 运算符进入队列
                num = 0;
            } else {
                // 遇到左括号，直接拿去计算
                int r[] = cal(s, i + 1);
                num = r[0]; // 获得返回值
                i = r[1] + 1; // i跳转到下标
            }
        }
        addNum(que, num); //最后一个数字加入
        return new int[]{getNum(que), i}; //计算出结果， 末尾位置
    }

    private static void addNum(LinkedList<String> que, int num) {
        if (!que.isEmpty()) {
            String top = que.pollLast();
            if (top.equals("*")) {
                int cur = Integer.valueOf(que.pollLast());
                num = cur * num;
            } else if (top.equals("/")) {
                int cur = Integer.valueOf(que.pollLast());
                num = cur / num;
            } else {
                que.addLast(top);
            }
        }
        que.addLast(String.valueOf(num));
    }

    private static int getNum(LinkedList<String> que) {
        int sum = 0;
        while (que.size() > 1) {
            int a = Integer.parseInt(que.removeFirst());
            String b = que.removeFirst();
            int c = Integer.parseInt(que.removeFirst());
            if (b.equals("+")) {
                sum = (a + c);
            } else {
                sum = (a - c);
            }
            que.addFirst(String.valueOf(sum));
        }
        return Integer.parseInt(que.removeFirst());
    }
}
