package LeetCode;

import java.util.Arrays;

public class P421_数组中两个数的最大异或值 {
    public int findMaximumXOR(int[] nums) {
        build(nums);
        int max = 0; // 可以和自己异或
        for (int num : nums) {
            max = Math.max(max, maxOr(num));
        }
        clear();
        return max;
    }

    static int high;


    static int n = 2888881;
    static int m = 2;
    static int[][] tree = new int[n][m];
    static int cnt = 1;

    public static void build(int[] nums){
        int max = Integer.MIN_VALUE;
        for (int num : nums) {
            max = Math.max(max, num);
        }
        high = 31 - Integer.numberOfLeadingZeros(max); // 去除前导0
        for (int num : nums) {
            insert(num);
        }
    }
    public static void insert(int num){
        int cur = 1;
        for (int i = high, path; i >= 0; i--) {
            path = (num >> i) & 1;
            if(tree[cur][path] == 0){
                tree[cur][path] = ++cnt;
            }
            cur = tree[cur][path];
        }
    }

    public static int maxOr(int num){
        int ans = 0;
        int cur = 1;
        for (int i = high, want, status; i >= 0; i--) {
            // 需要相反的路：num 第i位 状态
            status = (num >> i) & 1;
            want = (status) ^ 1; // 需要找到的是0，需要找到的是1
            if(tree[cur][want] == 0){ // 没有找到，走另一条路
                want ^= 1; // 找不到哦
            }
            ans |= (status ^ want) << i;
            cur = tree[cur][want];
        }
        return ans;
    }

    public static void clear(){
        for (int i = 0; i < cnt; i++) {
            Arrays.fill(tree[i], 0);
        }
        cnt = 1;
    }
}
