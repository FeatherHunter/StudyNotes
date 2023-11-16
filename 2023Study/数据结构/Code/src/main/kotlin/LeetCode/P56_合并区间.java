package LeetCode;

import java.util.*;

// 8ms 击败 34.21%使用 Java 的用户
public class P56_合并区间 {
    public int[][] merge(int[][] intervals) {

        Arrays.sort(intervals, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                return o1[0] - o2[0];
            }
        }); // 1、原始区间，开始位置排序

        List<int[]> ans = new ArrayList<>();
        PriorityQueue<Integer> heap = new PriorityQueue<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2 - o1;
            }
        });// 2、 大顶堆，处理各个区间
        for (int i = 0; i < intervals.length; i++) {
            if(heap.isEmpty()){
                heap.add(intervals[i][0]);
                heap.add(intervals[i][1]);
            }else{
                if(heap.peek() >= intervals[i][0]){
                    if(heap.peek() < intervals[i][1]){
                        heap.poll(); // 出前面尾巴
                        heap.add(intervals[i][1]); // 加入最大尾巴
                    }
                }else{
                    // 前面区间结尾和当前区间分离
                    int end = heap.poll();
                    int start = heap.poll();
                    int[] res = new int[]{start, end};
                    ans.add(res); // 添加结果

                    // 加入当前区间
                    heap.add(intervals[i][0]);
                    heap.add(intervals[i][1]);
                }
            }
        }
        // 3、最后一组，需要手动操作
        int end = heap.poll();
        int start = heap.poll();
        int[] res = new int[]{start, end};
        ans.add(res); // 添加结果

        int[][] result = new int[ans.size()][2];
        for (int i = result.length - 1; i >= 0 ; i--){
            result[i] = ans.remove(ans.size() - 1);
        }
        return result;
    }
}
