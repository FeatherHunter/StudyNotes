package LeetCode;

public class P263_丑数 {

    public boolean isUgly(int n) {
        if(n < 1) return false;
        if(n <= 6){
            return true;
        }
        while (n > 6){
            boolean flag = false;
            if(n % 5 == 0){
                flag = true;
                n = n / 5;
            }
            if(n % 3 == 0){
                flag = true;
                n = n / 3;
            }
            if(n % 2 == 0){
                flag = true;
                n = n / 2;
            }
            if(flag == false) return false; // 找不到一个可以除的，完蛋哦
        }
        return true;
    }
}
