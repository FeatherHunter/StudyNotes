package LeetCode;

public class P8_字符串转换整数 {
    public static int myAtoi(String s) {
        char[] a = s.trim().toCharArray();
        if(a.length <= 0) return 0;
        int flag = 1;
        long sum = 0;
        for (int i = 0; i < a.length; i++) {
            if(i == 0){
                if(a[0] == '-'){
                    flag = -1;
                    continue;
                }else if(a[0] == '+'){
                    flag = 1;
                    continue;
                }
            }
            if(a[i] < '0' || a[i] > '9'){
                break;
            }
            sum = sum * 10 + (a[i] - '0');
            if(sum > Integer.MAX_VALUE){
                break;
            }
        }
        sum = sum * flag;
        if(sum < Integer.MIN_VALUE) return Integer.MIN_VALUE;
        if(sum > Integer.MAX_VALUE) return Integer.MAX_VALUE;
        return (int) sum;
    }
}
