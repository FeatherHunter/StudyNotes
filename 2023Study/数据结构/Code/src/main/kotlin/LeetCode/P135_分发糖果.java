package LeetCode;

public class P135_分发糖果 {
    public int candy(int[] ratings) {
        // 1、左坡
        int[] left = new int[ratings.length];
        left[0] = 1;
        for (int i = 1; i < ratings.length; i++) {
            if (ratings[i - 1] < ratings[i]) { // 1. 比左边大 +1
                left[i] = left[i - 1] + 1;
            } else {
                left[i] = 1;
            }
        }
        // 2、右坡
        int[] right = new int[ratings.length];
        right[ratings.length - 1] = 1;
        for (int i = ratings.length - 2; i >= 0; i--) {
            if (ratings[i] > ratings[i + 1]) { // 1. 比左边大 +1
                right[i] = right[i + 1] + 1;
            } else {
                right[i] = 1;
            }
        }
        // 3、取最大值
        int sum = 0;
        for (int i = 0; i < ratings.length; i++) {
            sum += Math.max(left[i], right[i]);
        }
        return sum;
    }
}
