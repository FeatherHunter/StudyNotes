package LeetCode;

public class P1281_整数的各位积和之差KT {
    fun subtractProductAndSum(n: Int): Int {
        var num = n
        var sum = 0
        var cj = 1
        while (num > 0){
            sum += num % 10
            cj *= num % 10
            num /= 10
        }
        return cj - sum
    }
}
