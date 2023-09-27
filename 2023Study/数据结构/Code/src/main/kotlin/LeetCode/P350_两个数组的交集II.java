package LeetCode;

import java.util.Arrays;

public class P350_两个数组的交集II {
    /**
     * 题目意思:
     * [1,1,2,2] [2,2]
     * 1. 合集是 [2]
     * 2. 但是2的数量需要是共同出现的次数，结果为[2,2]
     * 1ms 击败 98.61%使用 Java 的用户
     */
    static int[] map = new int[1001];

    public int[] intersect(int[] nums1, int[] nums2) {
        Arrays.fill(map, 0);
        for (int n1 : nums1) {
            map[n1]++; // count++，后面遍历第二个字符串的时候，count！=0就可以加入结果
        }
        int[] res = new int[nums2.length];
        int index = 0;
        for (int n2 : nums2) {
            if (map[n2] > 0) {
                res[index++] = n2;
                map[n2]--; // 避免重复添加
            }
        }
        return Arrays.copyOf(res, index);
    }

}
