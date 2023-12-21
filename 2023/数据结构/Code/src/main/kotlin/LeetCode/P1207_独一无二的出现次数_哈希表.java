package LeetCode;

import java.util.Arrays;
import java.util.HashSet;

/**
 * https://leetcode.cn/problems/unique-number-of-occurrences
 * 1ms
 * 击败 99.20%使用 Java 的用户
 */
public class P1207_独一无二的出现次数_哈希表 {
    static int[] map = new int[2001];
    public boolean uniqueOccurrences(int[] arr) {
        Arrays.fill(map, 0);
        HashSet<Integer> set = new HashSet<>(); //去重
        for (int i = 0; i < arr.length; i++) {
            map[arr[i] + 1000]++;
            set.add(arr[i]);
        }

        HashSet<Integer> times = new HashSet<>(); //出现次数，要求不能一样
        for (Integer num : set) {
            if(!times.add(map[num + 1000])){
                // 不能添加有问题哦
                return false;
            }
        }
        return true;
    }
}
