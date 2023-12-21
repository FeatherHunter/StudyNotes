package LeetCode;

public class P57_插入区间 {
    public int[][] insert(int[][] intervals, int[] newInterval) {
        int[][] temp = new int[intervals.length + 1][2];
        int index = 0;
        boolean flag = false;
        for (int i = 0; i < intervals.length; i++) {
            if (index > 0) {
                // 区间的start比temp中前一个end小，说明被改变导致1（处理了newInterval），合并处理
                if (intervals[i][0] <= temp[index - 1][1]) {
                    if (intervals[i][1] > temp[index - 1][1]) {
                        temp[index - 1][1] = intervals[i][1];
                    }
                    continue;
                }
            }
            // 区间比较赋值
            if (intervals[i][1] < newInterval[0]) {
                temp[index][0] = intervals[i][0];
                temp[index][1] = intervals[i][1];
                index++;
            } else {
                if (intervals[i][0] > newInterval[1]) {
                    if (!flag) {
                        temp[index][0] = newInterval[0];
                        temp[index][1] = newInterval[1];
                        index++;
                        temp[index][0] = intervals[i][0];
                        temp[index][1] = intervals[i][1];
                        index++;
                        flag = true;
                    } else {
                        temp[index][0] = intervals[i][0];
                        temp[index][1] = intervals[i][1];
                        index++;
                    }
                    continue;
                }
                if (intervals[i][0] <= newInterval[0]) {
                    temp[index][0] = intervals[i][0];
                } else {
                    temp[index][0] = newInterval[0];
                }
                if (intervals[i][1] >= newInterval[1]) {
                    temp[index][1] = intervals[i][1];
                } else {
                    temp[index][1] = newInterval[1];
                }
                index++;
                flag = true;
            }
        }
        if (index == 0 || !flag) { // 考虑原始空，或者，新数组在最右区间
            temp[index][0] = newInterval[0];
            temp[index][1] = newInterval[1];
            index++;
        }
        int[][] res = new int[index][2];
        for (int i = 0; i < res.length; i++) {
            res[i] = Arrays.copyOf(temp[i], 2);
        }
        return res;
    }

}
