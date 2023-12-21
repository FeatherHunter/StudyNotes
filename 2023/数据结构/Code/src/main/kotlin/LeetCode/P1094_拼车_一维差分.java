package LeetCode;

import java.util.Arrays;

public class P1094_拼车_一维差分 {
    /**
     * 车上最初有 capacity 个空座位。车 只能 向一个方向行驶（也就是说，不允许掉头或改变方向）
     * 给定整数 capacity 和一个数组 trips ,  trip[i] = [numPassengersi, fromi, toi] 表示第 i 次旅行有 numPassengersi 乘客，接他们和放他们的位置分别是 fromi 和 toi 。
     * 这些位置是从汽车的初始位置向东的公里数。
     */
    static int[] AdjacentDifference = new int[1001];
    public boolean carPooling(int[][] trips, int capacity) {
        Arrays.fill(AdjacentDifference, 0);
        for (int i = 0; i < trips.length; i++) {
            AdjacentDifference[trips[i][1]] += trips[i][0];
            AdjacentDifference[trips[i][2]] -= trips[i][0];
        }
        for (int i = 1; i < AdjacentDifference.length; i++) {
            AdjacentDifference[i] += AdjacentDifference[i-1];
        }
        for (int numsPassenger : AdjacentDifference) {
            if(numsPassenger > capacity){
                return false; // 带不了
            }
        }
        return true;
    }
}
