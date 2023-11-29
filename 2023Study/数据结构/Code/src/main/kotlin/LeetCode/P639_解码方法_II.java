package LeetCode;

import java.util.Arrays;

public class P639_解码方法_II {

    /**
     *三省吾身：
     *1. 注意到了什么？ => 1.需要从当前位置i到末尾，s[i....n-1] 2.需要考虑所有情况 3.需要取模 4.需要考虑边界情况
     *2. 采取了什么行动？ => 编码中罗列可能的场景
     *3. 犯了哪些错误？ =>
     *  1.题目就没看明白
     *  2.需要提前分析场景，罗列好
     *  3.场景太多了，很混乱【这个是没问题的】
     *  4.取模错误，应该用long去装int值
     *4. 可以怎样去改变？ => 1.多看一些题目，自己仔细去理解。 2. 是否应该用递归方式去尝试？【用递归操作】
     */
    public int numDecodings(String s) {
        return (int) f4(s.toCharArray());
    }
    // 返回从w[i...]到末尾一共有多少种方案
    // 版本一：迭代
    public long f1(char[] w, int i){
        // 1、边界
        if(i >= w.length) return 1; //达到末尾，1种方案（不能是0，都到末尾了有1个方案很正常哦） => 事后思考一下
        if(w[i] == '0') return 0; // 没有方案别考虑了
        // 2、场景思考
        // 3、单独考虑i
        long ans = ((w[i] == '*')? 9 : 1) * f1(w, i+1); //
        // 4、组合考虑i(需要存在i+1)
        if(i + 1 < w.length){
            // a. i=num, i+1=num, 在11~26的场景中，1*f1(i+2)
            if(w[i] >= '0' && w[i] <= '9' && w[i+1] >= '0' && w[i+1] <= '9'){
                int num = (w[i]-'0')*10+(w[i+1]-'0');
                if(num >= 10 && num <= 26){
                    ans += f1(w, i+2);
                }
            }
            // b. i=num, i+1=*
            //      b-1 i=1, 11~19 9*f1(i+2)
            //      b-2 i=2, 21~26 6*f1(i+2)
            //      b-3 i=3++, 0个
            else if(w[i] >= '0' && w[i] <= '9' && w[i+1] == '*'){
                if(w[i] == '1'){
                    ans += 9 * f1(w, i+2);
                }else if (w[i] == '2'){
                    ans += 6 * f1(w, i+2);
                }else{
                    ans += 0;
                }
            }
            // c. i=*, i+1=num
            //      c-1 i+1 = 0~6 2*f1(i+2)
            //      c-2 i+1 = 7~9 1*f1(i+2)
            else if(w[i] == '*' && w[i+1] >= '0' && w[i+1] <= '9'){
                if(w[i+1] >= '0' && w[i+1] <= '6'){
                    ans += 2 * f1(w, i+2);
                }else if (w[i+1] >= '7' && w[i+1] <= '9'){
                    ans += 1 * f1(w, i+2);
                }
            }
            // d. i=*, i+1=*
            //      11~19 + 21~26 = 15*f1(i+2)
            else if(w[i] == '*' && w[i+1] == '*'){
                ans += 15 * f1(w, i+2);
            }
        }
        ans = ans % 1000000007;
        return ans;
    }

    // 版本二：懒缓存版本
    public long f2(char[] w, int i, long dp[]){
        // 1、边界
        if(i >= w.length) return 1; //达到末尾，1种方案（不能是0，都到末尾了有1个方案很正常哦） => 事后思考一下
        if(w[i] == '0') return 0; // 没有方案别考虑了
        if(dp[i] != -1){
            return dp[i];
        }
        // 2、场景思考
        // 3、单独考虑i
        long ans = ((w[i] == '*')? 9 : 1) * f2(w, i+1, dp); //
        // 4、组合考虑i(需要存在i+1)
        if(i + 1 < w.length){
            // a. i=num, i+1=num, 在11~26的场景中，1*f2(i+2)
            if(w[i] >= '0' && w[i] <= '9' && w[i+1] >= '0' && w[i+1] <= '9'){
                int num = (w[i]-'0')*10+(w[i+1]-'0');
                if(num >= 10 && num <= 26){
                    ans += f2(w, i+2, dp);
                }
            }
            // b. i=num, i+1=*
            //      b-1 i=1, 11~19 9*f2(i+2)
            //      b-2 i=2, 21~26 6*f2(i+2)
            //      b-3 i=3++, 0个
            else if(w[i] >= '0' && w[i] <= '9' && w[i+1] == '*'){
                if(w[i] == '1'){
                    ans += 9 * f2(w, i+2, dp);
                }else if (w[i] == '2'){
                    ans += 6 * f2(w, i+2, dp);
                }else{
                    ans += 0;
                }
            }
            // c. i=*, i+1=num
            //      c-1 i+1 = 0~6 2*f2(i+2)
            //      c-2 i+1 = 7~9 1*f2(i+2)
            else if(w[i] == '*' && w[i+1] >= '0' && w[i+1] <= '9'){
                if(w[i+1] >= '0' && w[i+1] <= '6'){
                    ans += 2 * f2(w, i+2, dp);
                }else if (w[i+1] >= '7' && w[i+1] <= '9'){
                    ans += 1 * f2(w, i+2, dp);
                }
            }
            // d. i=*, i+1=*
            //      11~19 + 21~26 = 15*f2(i+2)
            else if(w[i] == '*' && w[i+1] == '*'){
                ans += 15 * f2(w, i+2, dp);
            }
        }
        ans = ans % 1000000007;
        dp[i] = ans;
        return ans;
    }
    // 版本三：dp数组
    public long f3(char[] w){
        int N = w.length;
        long dp[] = new long[N+1];
        // 1、边界
        dp[N] = 1;
        for (int i = N-1; i >= 0; i--) {
            if(w[i] == '0'){
                dp[i] = 0;
                continue;
            }
            long ans = ((w[i] == '*')? 9 : 1) * dp[i+1]; //
            if(i + 1 < N){
                // a. i=num, i+1=num, 在11~26的场景中，1*f2(i+2)
                if(w[i] >= '0' && w[i] <= '9' && w[i+1] >= '0' && w[i+1] <= '9'){
                    int num = (w[i]-'0')*10+(w[i+1]-'0');
                    if(num >= 10 && num <= 26){
                        ans += dp[i+2];
                    }
                }
                // b. i=num, i+1=*
                //      b-1 i=1, 11~19 9*f2(i+2)
                //      b-2 i=2, 21~26 6*f2(i+2)
                //      b-3 i=3++, 0个
                else if(w[i] >= '0' && w[i] <= '9' && w[i+1] == '*'){
                    if(w[i] == '1'){
                        ans += 9 * dp[i+2];
                    }else if (w[i] == '2'){
                        ans += 6 * dp[i+2];
                    }else{
                        ans += 0;
                    }
                }
                // c. i=*, i+1=num
                //      c-1 i+1 = 0~6 2*f2(i+2)
                //      c-2 i+1 = 7~9 1*f2(i+2)
                else if(w[i] == '*' && w[i+1] >= '0' && w[i+1] <= '9'){
                    if(w[i+1] >= '0' && w[i+1] <= '6'){
                        ans += 2 * dp[i+2];
                    }else if (w[i+1] >= '7' && w[i+1] <= '9'){
                        ans += 1 * dp[i+2];
                    }
                }
                // d. i=*, i+1=*
                //      11~19 + 21~26 = 15*f2(i+2)
                else if(w[i] == '*' && w[i+1] == '*'){
                    ans += 15 * dp[i+2];
                }
            }
            ans = ans % 1000000007;
            dp[i] = ans;
        }
        return dp[0];
    }

    // 版本四：dp+空间节省
    public long f4(char[] w){
        int N = w.length;
        // 1、边界
        long cur = 0;
        long next = 1;
        long nextnext = 1;
        for (int i = N-1; i >= 0; i--) {
            if(w[i] == '0'){
                cur = 0;
                nextnext = next;
                next = cur;
                continue;
            }
            long ans = ((w[i] == '*')? 9 : 1) * next; //
            if(i + 1 < N){
                // a. i=num, i+1=num, 在11~26的场景中，1*f2(i+2)
                if(w[i] >= '0' && w[i] <= '9' && w[i+1] >= '0' && w[i+1] <= '9'){
                    int num = (w[i]-'0')*10+(w[i+1]-'0');
                    if(num >= 10 && num <= 26){
                        ans += nextnext;
                    }
                }
                // b. i=num, i+1=*
                //      b-1 i=1, 11~19 9*f2(i+2)
                //      b-2 i=2, 21~26 6*f2(i+2)
                //      b-3 i=3++, 0个
                else if(w[i] >= '0' && w[i] <= '9' && w[i+1] == '*'){
                    if(w[i] == '1'){
                        ans += 9 * nextnext;
                    }else if (w[i] == '2'){
                        ans += 6 * nextnext;
                    }else{
                        ans += 0;
                    }
                }
                // c. i=*, i+1=num
                //      c-1 i+1 = 0~6 2*f2(i+2)
                //      c-2 i+1 = 7~9 1*f2(i+2)
                else if(w[i] == '*' && w[i+1] >= '0' && w[i+1] <= '9'){
                    if(w[i+1] >= '0' && w[i+1] <= '6'){
                        ans += 2 * nextnext;
                    }else if (w[i+1] >= '7' && w[i+1] <= '9'){
                        ans += 1 * nextnext;
                    }
                }
                // d. i=*, i+1=*
                //      11~19 + 21~26 = 15*f2(i+2)
                else if(w[i] == '*' && w[i+1] == '*'){
                    ans += 15 * nextnext;
                }
            }
            ans = ans % 1000000007;
            cur = ans;
            nextnext = next;
            next = cur;
        }
        return cur;
    }
}
