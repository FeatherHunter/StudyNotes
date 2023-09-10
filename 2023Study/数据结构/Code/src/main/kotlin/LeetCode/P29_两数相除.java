package LeetCode;

public class P29_两数相除 {
    public int divide(int dividend, int divisor) {
        long result = (long)dividend / divisor;
        if(result < Integer.MIN_VALUE){
            result = Integer.MIN_VALUE;
        }else if(result > Integer.MAX_VALUE){
            result = Integer.MAX_VALUE;
        }
        return (int) result;
    }
}
