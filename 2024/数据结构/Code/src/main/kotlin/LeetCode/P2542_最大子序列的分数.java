package LeetCode;

import java.util.Arrays;
import java.util.PriorityQueue;

public class P2542_最大子序列的分数 {

    // nums1 = [1,3,3,2], nums2 = [2,1,3,4], k = 3
    // 排序后 成为 [4,3,2,1] 对应的nums1变成了 [2 3 1 3]
    // nums1的2放入，3放入 顺序就是 2 3，此时sum = 2+3=5
    // 放入 1，此时sum=6,6需要x2，此时的最小值 = 12 =》123需要去掉1因此就是 23咯
    // 第二次循环，此时要存入3，就是 2 3 3.此时sum = 2+3+3=8，此时需要8*1
    public long maxScore(int[] nums1, int[] nums2, int k) {
        Integer[] ids = new Integer[nums2.length];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = i;
        }
        // 进行降序排序
        Arrays.sort(ids,(a,b)->nums2[b]-nums2[a]);

        long sum = 0;
        // 先填充k-1个数据
        PriorityQueue<Integer> heap = new PriorityQueue<>(); // 小顶堆
        for (int i = 0; i < k-1; i++) {
            sum += nums1[ids[i]];
            heap.offer(nums1[ids[i]]); // 放入了k-1个数据
        }
        long ans = 0;
        // 需要最大的nums1 的和 sum x 最小的 nums2
        // 因此是heap中的和 x nums2的当前值
        // heap一直在弹出最小的，此时 sum就一直会是最大的，因为x的nums2都是最小的，符合要求
        for (int i = k-1; i < nums1.length; i++) {
            sum += nums1[ids[i]];
            heap.offer(nums1[ids[i]]); // 放入数据
            ans = Math.max(ans, nums2[ids[i]] * sum); // 当前下标对应的nums2肯定是最大的
            sum -= heap.poll(); // 弹出最小的nums1
        }
        return ans;
    }
}
