package LeetCode;

import java.util.Arrays;

public class P349_两个数组的交集 {
    // 1ms 击败 98.44%使用 Java 的用户
    static boolean[] map = new boolean[1001];

    public int[] intersection(int[] nums1, int[] nums2) {
        Arrays.fill(map, false);
        for (int n1 : nums1) {
            map[n1] = true;
        }
        int[] res = new int[nums2.length];
        int index = 0;
        for (int n2 : nums2) {
            if (map[n2]) {
                res[index++] = n2;
                map[n2] = false; // 避免重复添加
            }
        }
        return Arrays.copyOf(res, index);
    }

}
