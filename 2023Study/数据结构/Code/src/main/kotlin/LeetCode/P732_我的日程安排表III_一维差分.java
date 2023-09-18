package LeetCode;

import java.util.Arrays;

public class P732_我的日程安排表III_一维差分 {
    /**
     * 当 k 个日程安排有一些时间上的交叉时（例如 k 个日程安排都在同一时间内），就会产生 k 次预订。
     *
     * 给你一些日程安排 [start, end) ，请你在每个日程安排添加后，返回一个整数 k ，表示所有先前日程安排会产生的最大 k 次预订。
     *
     * 实现一个 MyCalendarThree 类来存放你的日程安排，你可以一直添加新的日程安排。
     *
     * MyCalendarThree() 初始化对象。
     * int book(int start, int end) 返回一个整数 k ，表示日历中存在的 k 次预订的最大值。
     */
    static class MyCalendarThree {
        int MAXN = 142857143; // 7
        long[] ans = new long[MAXN]; // book最多400次，也就是k最多400个哦。      512 256  128 64 32 16 8 4 2 1 // 9bit哦 32/9 = 3
        int max = 0;

        public MyCalendarThree() {

        }

        public int book(int startTime, int endTime) {
            for (int i = startTime; i < endTime; i++) {
                max = (int) Math.max(max, add(i)); // 最大预定次数
            }
            return max;
        }
        // 时间time在数组中下标里面，+1
        public int add(int time){
            int index = time / 7; //
            int offset = time % 7;
            long src = ans[index]; // 元数据数据，需要在目标位置—++哦
            src >>= 7 * offset;
            src &= (0x1ff);
            src++;
            int res = (int) src;
            src <<= 7 * offset;
            ans[index] |= src;
            return res;
        }
    }
}
