package LeetCode;

/**
 * https://leetcode.cn/problems/zigzag-conversion/submissions/
 */
public class P6_N字形变换 {
    public static String convert(String s, int numRows) {
        if(numRows == 1) return s;
        char[] arr = s.toCharArray();
        StringBuilder[] builders = new StringBuilder[numRows];
        for (int i = 0; i < builders.length; i++) {
            builders[i] = new StringBuilder();
        }
        int add = 1;
        int index = 0;
        for (int i = 0; i < arr.length; i++) {
            builders[index].append(arr[i]);
            index += add;
            if(index == 0 || index == numRows - 1){
                add = -add;
            }
        }
        String result = "";
        for (StringBuilder builder : builders) {
            result += builder.toString();
        }
        return result;
    }
}
