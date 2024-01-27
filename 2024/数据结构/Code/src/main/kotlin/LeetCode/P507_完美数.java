package LeetCode;

import java.util.ArrayList;
import java.util.List;

public class P507_完美数 {
    public boolean checkPerfectNumber(int num) {
        if(num == 1) return false;
        int sum = 1;
        for (int i = 2; i <= num / i; i++) {
            if(num % i == 0){
                sum += i;
                if(i * i != num){
                    sum += num / i;
                }
            }
        }
        return sum == num;
    }
}
