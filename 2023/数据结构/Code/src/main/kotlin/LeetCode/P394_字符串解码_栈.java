package LeetCode;

import java.util.ArrayDeque;
import java.util.Deque;

public class P394_字符串解码_栈 {
    public String decodeString(String s) {
        char[] a = s.toCharArray();
        Deque<Integer> numStack = new ArrayDeque<>(); // 3[xxx]的数字3 栈
        Deque<String> strStack = new ArrayDeque<>();
        int num = 0;
        String temp = "";
        for (int i = 0; i < a.length; i++) {
            if (a[i] == '[') {
                // 数字入栈
                numStack.push(num);
                num = 0; // 数字需要清空
            } else if (a[i] <= '9' && a[i] >= '0') {
                if (!temp.isEmpty()) {
                    strStack.push(temp);
                    temp = ""; // 字符串需要清空
                }
                num = num * 10 + (a[i] - '0');
            } else if (a[i] <= 'z' && a[i] >= 'a') {
                temp += a[i];
            } else {
                if (!temp.isEmpty()) {
                    strStack.push(temp);
                    temp = "";// 字符串需要清空
                }
                // 就是]
                int n = numStack.pop();
                String temple = strStack.pop();
                String str = "";
                while (n-- > 0) {
                    str = str + temple;
                }
                if (!numStack.isEmpty()) {
                    str = strStack.pop() + str; // 需要考虑3[a2[c]]中 需要把acc放入的情况
                }
                // 计算完成后加入
                strStack.push(str);
            }
        }
        // 需要处理末尾可能还有字符串
        if (!temp.isEmpty()) {
            strStack.push(temp);
        }
        // 清理字符串栈
        String res = "";
        while (!strStack.isEmpty()) {
            res = strStack.pop() + res;
        }
        return res;
    }

}
