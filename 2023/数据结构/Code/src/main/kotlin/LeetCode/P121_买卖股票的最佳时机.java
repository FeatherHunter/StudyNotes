package LeetCode;

/**
 * 单调栈: 维护前方最小的原则。
 */
public class P121_买卖股票的最佳时机 {

    static int MAIN = 10001;
    static int[] stack = new int[MAIN];
    static int index = -1;
    public static int maxProfit(int[] prices) {

        index = -1;

        int max = 0;

        for (int i = 0; i < prices.length - 1; i++) {
            if(index == -1 || prices[stack[index]] > prices[i]){
                stack[++index] = i;
            }
            // 存放下标
            max = Math.max(prices[i + 1] - prices[stack[index]], max);
        }
        return max;
    }
}
