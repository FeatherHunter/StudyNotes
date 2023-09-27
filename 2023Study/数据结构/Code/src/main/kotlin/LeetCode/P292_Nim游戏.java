package LeetCode;

public class P292_Nim游戏 {
    // 对数器法
    public boolean canWinNim(int n) {
        return n % 4 != 0;
    }
    // 动态规划+空间节约,超时
    public boolean canWinNimDp(int n) {
        if (n <= 3) return true;
        boolean dp1 = true;
        boolean dp2 = true;
        boolean dp3 = true;
        boolean res;
        for (int i = 4; i <= n; i++) {
            res = !dp1 || !dp2 || !dp3;
            dp1 = dp2;
            dp2 = dp3;
            dp3 = res;
        }
        return dp3;
    }

    // dp[n] 1~n,赢得概率是多少

    // 先手我赢,递归版本
    public boolean firstTake(int n){
        if(n <= 3) return true;
        boolean p1 = !firstTake(n - 3);
        boolean p2 = !firstTake(n - 2);
        boolean p3 = !firstTake(n - 1);
        return p1 || p2 || p3; // 赢了一种就行了
    }
}
