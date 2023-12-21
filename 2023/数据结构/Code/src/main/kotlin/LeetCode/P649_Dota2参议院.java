package LeetCode;

/**
 * https://leetcode.cn/problems/dota2-senate/
 * 4ms 击败 86.10%使用 Java 的用户
 */
public class P649_Dota2参议院 {

    static char[] queue = new char[10001];
    static int head = 0;
    static int tail = 0;

    public String predictPartyVictory(String senate) {

        head = tail = 0;

        char[] a = senate.toCharArray();
        int rsize = 0;
        int dsize = 0;
        for (int i = 0; i < a.length; i++) {
            queue[tail++] = a[i];
            if(a[i] == 'R'){
                rsize++;
            }else{
                dsize++;
            }
        }

        int rc = 0; // 失去权利R的数量
        int dc = 0;

        while (rsize > 0 && dsize > 0){
            char c = queue[head++];
            if(c == 'R'){
                if(rc > 0){
                    rc--;
                    rsize--;
                }else{
                    // 自己有权利
                    dc++;
                    queue[tail++] = c;
                }
            }else{
                if(dc > 0){
                    dc--;
                    dsize--;
                }else{
                    rc++;
                    queue[tail++] = c;
                }
            }

            if(tail == queue.length){
                tail = 0; // 从头来过
            }
            if(head == queue.length){
                head = 0; // 从头来过
            }
        }
        if(rsize > 0){
            return "Radiant";
        }else{
            return "Dire";
        }
    }
}
