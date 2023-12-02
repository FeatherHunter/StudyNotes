package LeetCode;

public class P32_最长有效括号 {

    //())()
    //02006


    /**
     * ( , dp=0
     * ) , i==0, dp=0
     * (), 符合要求，且没有i-2, = 2
     *      符合要求，有i-2, ((),=2
     *      符合要求，有i-2, )(),=dp[i-2]+2 //
     * )), 不符合要求，dp[i-1] = 0 , dp = 0 // 左侧没有健康的了
     * ()), dp[i-1] = 2
     *    (()) // i-1-2 = '(' 合法，dp = dp[i-1] + 2
     *      ()()(()) // 需要考虑左侧的合法长度也会算在当前位置，需要对dp[i]进行更新
     *    )()) // i-1-2 = ')' 不合法,dp = 0
     */

    /**
     * 1. 注意到了什么？需要dp、需要考虑所有情况、考虑边界
     * 2. 采取了什么行动？
     * 3. 犯了哪些错误？1. 没有罗列所有场景，凭感觉测试 2.考虑边界（不完善） 3.根本不是根据底部，而是找【最大值】。不能取dp[n-1]
     * 4. 可以怎样去改变？
     * 1. 罗列所有场景
     * 2. 边界都在开头处理
     */
    public int longestValidParenthesesOld(String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }
        char[] w = s.toCharArray();
        int n = w.length;
        int dp[] = new int[n];

        int max = 0;
        for (int i = 0; i < n; i++) {
            if (w[i] == '(') { // ( , dp=0
                dp[i] = 0;
            } else { //  w[i] == ')'
                if (i == 0) { // 不存在i-1
                    dp[i] = 0;
                } else {// 存在i-1
                    if (w[i - 1] == '(') {
                        if (i - 2 >= 0) { // 存在i-2
                            dp[i] = 2 + dp[i - 2]; // w[i-2] = '('就是 2 + 0
                        } else { // (), 符合要求，且没有i-2, = 2
                            dp[i] = 2;
                        }

                    } else {
                        // w[i-1] == ')'
                        if (dp[i - 1] != 0 && (i - 1 - dp[i - 1]) >= 0 && w[i - 1 - dp[i - 1]] == '(') {
                            dp[i] = dp[i - 1] + 2;
                            if (i - 1 - dp[i] >= 0) {
                                dp[i] = dp[i] + dp[i - 1 - dp[i]];
                            }
                        } else {
                            dp[i] = 0;
                        }
                    }
                }
            }
            max = Math.max(max, dp[i]);
        }
//        return dp[n-1]; // 需要考虑边界
        return max;//返回的是最大值
    }

    // 1 ms 击败 100.00% 使用 Java 的用户
    public int longestValidParenthesesG1(String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }
        char[] w = s.toCharArray();
        int n = w.length;
        int dp[] = new int[n];

        int max = 0;
        for (int i = 0; i < n; i++) {
            if (w[i] != '(' && i > 0) {
                if (w[i - 1] == '(') { // '()'情况=2
                    dp[i] = 2;
                } else { // //‘))’
                    // w[i-1] == ')'  // ))
                    if ((i - 1 - dp[i - 1]) >= 0 && w[i - 1 - dp[i - 1]] == '(') {
                        dp[i] = dp[i - 1] + 2;
                    }
                }
                // 自我更新
                if (i - dp[i] >= 0) {
                    dp[i] = dp[i] + dp[i - dp[i]];
                }
            }
            max = Math.max(max, dp[i]);
        }
        return max;//返回的是最大值
    }

    public int longestValidParentheses(String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }
        char[] w = s.toCharArray();
        int n = w.length;
        int dp[] = new int[n];

        int max = 0;
        for (int i = 1; i < n; i++) {
//            if (w[i] != '(' && i > 0) {
            if (w[i] == ')') { // 从i=1开始，才是有价值的，不需要考虑i-1
                // 目标位置：p = dp[i-1]
                int pre = i-1-dp[i-1];
                // 思路：根据dp[i-1]找到要找的目标位置，来和自己比较，如果为（代表合法，dp[i]=dp[i-1] + 2,并且需要考虑
                if(pre >= 0 && w[pre] == '('){
                    dp[i] = dp[i-1] + 2 + ((pre-1>=0)?dp[pre-1]:0);
                }
            }
            max = Math.max(max, dp[i]);
        }
        return max;//返回的是最大值
    }
}
