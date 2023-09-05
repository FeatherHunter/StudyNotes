package LeetCode;

/**
 * https://leetcode.cn/problems/calculate-money-in-leetcode-bank/
 * 等差数列求和公式 S = (n/2) * (2a + (n-1)d) a=首项 b=差 n=项数
 */
public class P1716_calculate_money_in_leetcode_bank {
    public static int totalMoney(int n) {

        int week = n / 7;
        int rest = n % 7;
        int total = 0;
        int start = week + 1;
        for (int i = 0; i < rest; i++) {
            total += start;
            start++;
        }
        if(week > 0){
            total += week * (1+2+3+4+5+6+7) + (week/2.0)*((week-1)*7);
        }

        return total;
    }

}
