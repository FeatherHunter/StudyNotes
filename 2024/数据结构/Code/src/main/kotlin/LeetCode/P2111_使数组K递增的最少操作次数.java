package LeetCode;

import java.util.Arrays;

public class P2111_使数组K递增的最少操作次数 {
    public int kIncreasing(int[] arr, int k) {
        int[][] ends = new int[k][(arr.length+k)/k];
        int count = 0;
        int size[] = new int[k];
        for (int i = 0; i < arr.length; i++) {
            int num = arr[i];
            int index = i % k;
            int find = dpfind(ends[index], size[index], num);
            if(find == -1){
                // 没有找到
                ends[index][size[index]++] = num;
            }else{
                // 找到了 > 她的数据,更新
                ends[index][find] = num;
                count++;
            }
        }
        return count; // 最少次数
    }

    public int dpfind(int[] ends, int size, int num){
        int l = 0;
        int r = size - 1;
        int ans = -1;
        while (l <= r){
            int m = (l+r)/2;
            if(ends[m] > num){
                ans = m;
                r = m - 1;
            }else{
                l = m + 1;
            }
        }
        return ans;
    }
}
