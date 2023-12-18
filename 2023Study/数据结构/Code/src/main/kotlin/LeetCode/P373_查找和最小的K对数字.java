package LeetCode;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class P373_查找和最小的K对数字{

    public List<List<Integer>> kSmallestPairs(int[] nums1, int[] nums2, int k) {
        // 1、构建堆。大小为k
        PriorityQueue<int[]> heap = new PriorityQueue<>(k, (o1, o2) -> {
            return (int)((long)nums1[o1[0]] + (long)nums2[o1[1]] - (long)nums1[o2[0]] - (long)nums2[o2[1]]);
        });
        // 2、塞入数据
        for (int a : nums1) {
            for (int b : nums2) {
                heap.offer(new int[]{a, b});
            }
        }
        // 3、取出数据
        List<List<Integer>> ans = new ArrayList<>();
        while (!heap.isEmpty()){
            int[] num = heap.poll();
            List<Integer> res = new ArrayList<>();
            res.add(num[0]);
            res.add(num[1]);
            ans.add(res);
        }
        return ans;
    }

//    public List<List<Integer>> kSmallestPairs(int[] nums1, int[] nums2, int k) {
//        List<List<Integer>> ans = new ArrayList<>();
//        long last = Long.MIN_VALUE; // 上一个符合要求的数值
//        int p1 = 0;
//        int p2 = 0;
//        // 找第一个最小
//        while (k > 0){
//            // 符合要求
//            List<Integer> res = new ArrayList<>();
//            res.add(nums1[p1]);
//            res.add(nums2[p2]);
//            ans.add(res);
//            k--;
//            // 找到一个后，调整p1、p2的位置
//            if(p1 + 1 < nums1.length){
//                if(p2 + 1 < nums2.length){
//                    if((long)nums1[p1 + 1] + (long)nums2[p2] < (long)nums1[p1] + (long)nums2[p2 + 1]){
//                        p1 = p1 + 1;
//                    }else{
//                        p2 = p2 + 1;
//                    }
//                }else{
//                    p1 = p1 + 1;
//                }
//            }else{
//                if(p2 + 1 < nums2.length){
//                    p2 = p2 + 1;
//                }else{
//                    break; // 都到了末尾
//                }
//            }
//        }
//        return ans;
//    }
}
