package LeetCode;

public class P367_有效的完全平方数 {
    // 0ms 击败 100.00%使用 Java 的用户
    // 二分法
    public boolean isPerfectSquare(int num) {
        if (num == 1) {
            return true;
        }
        int l = 2;
        int r = num / 2;
        while (l <= r) {
            int m = l + (r - l) / 2;
            if ((long) m * m > num) {
                r = m - 1;
            } else if ((long) m * m < num) {
                l = m + 1;
            } else {
                return true; // 找到了
            }
        }
        return false;
    }

}
