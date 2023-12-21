package LeetCode;

public class P258_各位相加 {
    public int addDigits(int num) {
        int res = 0;
        while (num > 9){
            while (num != 0){
                res += num % 10;
                num = num / 10;
            }
            num = res;
            res = 0; // 缩小一次
        }

        return num;
    }
}
