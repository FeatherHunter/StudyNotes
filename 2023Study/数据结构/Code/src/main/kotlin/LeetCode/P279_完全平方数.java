package LeetCode;

/**
 * 四平方定理：
 * 任何一个自然数，最多拆为4个完全平方数的和
 *
 * 扯淡题目
 */
public class P279_完全平方数 {
    public int numSquares(int n) {
        int count = 4;
        int max = (int) Math.sqrt(n);
        for (int i1 = max; i1 >= 1; i1--) {
            int n1 = n - i1 * i1; // n1 = 8
            if(n1 == 0){
                count = Math.min(count, 1);
                break; // 只有一个了，不用再找了
            }
            int max2 = (int) Math.sqrt(n1);
            for (int i2 = max2; i2 >= 1; i2--) {
                int n2 = n1 - i2 * i2; // n2 = 4
                if(n2 == 0){
                    count = Math.min(count, 2);
                    continue;
                }
                int max3 = (int) Math.sqrt(n2);
                for (int i3 = max3; i3 >= 1; i3--) {
                    int n3 = n2 - i3 * i3; // n3 = 0
                    if(n3 == 0){
                        count = Math.min(count, 3);
                        continue;
                    }
                    int max4 = (int) Math.sqrt(n3);
                    for (int i4 = max4; i4 >= 1; i4--) {
                        int n4 = n3 - i4 * i4;
                        if(n4 == 0){
                            count = Math.min(count, 4);
                            continue;
                        }// else代表没有结果
                    }
                }
            }
        }
        return count;
    }
}
