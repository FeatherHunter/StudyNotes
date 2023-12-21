package LeetCode;

import java.util.ArrayList;
import java.util.List;

public class P969_煎饼排序 {
    public List<Integer> pancakeSort(int[] arr) {
        int n = arr.length;
        List<Integer> ans = new ArrayList<>();
        int[] ids = new int[n + 1];
        for (int i = 0; i < arr.length; i++) {
            ids[arr[i]] = i;
        }
        // 思路：从1~n，最大的n，开始调整到他需要到的位置
        for (int i = n; i >= 1; i--) {
            int index = ids[i]; // 最大数的下标
            if (index == i - 1) {
                // 啥都不用做，完美的（n在n-1的位置上/i在i-1上）
            } else {
                // 将n从下标index反转到0
                // 将i从下标index反转到0
                if (index != 0) {
                    reverse(arr, ids, index); //index翻转到0
                    ans.add(index + 1);
                }
                // 将n反转到n-1的位置上，记录下n
                // 将i反转到i-1的位置上，记录下i
                reverse(arr, ids, i - 1); // 反转到num-1的地方
                ans.add(i);
            }
        }
        return ans;
    }

    public void reverse(int[] arr, int[] ids, int r) {
        int l = 0;
        while (l < r) {
            ids[arr[l]] = r; // 更改数字所在的下标
            ids[arr[r]] = l;
            arr[l] = arr[l] ^ arr[r]; // 交换
            arr[r] = arr[l] ^ arr[r];
            arr[l] = arr[l] ^ arr[r];
            l++;
            r--;
        }
    }
}
