package LeetCode;


import java.util.Arrays;

/**
 * https://leetcode.cn/problems/online-stock-span/
 * 17ms 击败 100.00%使用 Java 的用户
 */
public class P901_股票价格跨度 {
    // [100,80,60,70,60,75,85]
    // [1,1,1,2,1,4,6]
    // 找到左侧大于自己的，下标相减
    static class StockSpanner {

        static int[] stack = new int[10000];
        static int[] nums = new int[10000];
        static int top = 0;
        static int i = 0;

        public StockSpanner() {
            top = 0;
            i = 0;
        }

        public int next(int price) {
            nums[i] = price;
            while (top != 0 && nums[stack[top - 1]] <= price){
                top--;
            }
            stack[top++] = i;
            i++;
            if(top == 1){
                return i; // 只有自己 // 艰难完成了
            }else{
                return stack[top - 1] - stack[top - 2]; // 头部减去头部
            }
        }
    }
}
