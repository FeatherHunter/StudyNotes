package LeetCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * https://leetcode.cn/problems/find-the-difference-of-two-arrays
 * 3ms 击败 98.74%使用 Java 的用户
 */
public class P2215_找出两数组的不同_哈希表 {
    static int[] map1 = new int[2001];
    static int[] map2 = new int[2001];
    public List<List<Integer>> findDifference(int[] nums1, int[] nums2) {
        Arrays.fill(map1, 0);
        Arrays.fill(map2, 0);

        for (int i = 0; i < nums1.length; i++) {
            map1[nums1[i] + 1000] = 1;
        }
        List<Integer> ans2 = new ArrayList<>();
        for (int i = 0; i < nums2.length; i++) {
            map2[nums2[i] + 1000] = 1;
            if(map1[nums2[i] + 1000] != 1){
                ans2.add(nums2[i]); // 构造出答案2
                map1[nums2[i] + 1000] = 1; // 答案不能重复哦
            }
        }
        // 遍历1，构造出答案1
        List<Integer> ans1 = new ArrayList<>();
        for (int i = 0; i < nums1.length; i++) {
            if(map2[nums1[i] + 1000] != 1){
                ans1.add(nums1[i]);
                map2[nums1[i] + 1000] = 1; //答案不能重复
            }
        }
        List<List<Integer>> res = new ArrayList<>();
        res.add(ans1);
        res.add(ans2);
        return res;
    }
}
