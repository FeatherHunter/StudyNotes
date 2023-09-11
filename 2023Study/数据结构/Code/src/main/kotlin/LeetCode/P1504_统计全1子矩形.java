package LeetCode;

/**
 *
 * [1,1,1,1,0,1,0] 5+3+2+1=11
 * [1,1,1,0,0,0,1] 4+2+1+3+2+1=13
 * [0,1,1,1,1,0,0] 4+3+2+1+2+2+2=16
 * [1,1,0,1,1,0,1] 5+2+3+2+1=13
 * [1,0,0,0,0,0,1] 2+2=4
 * [1,1,0,1,1,1,1] 6+4+2+1+2+2=17
 * [1,1,0,0,1,1,1] 5+3+1+3+1+3+3+1=20 // 4 2 0 0 2 2 2 4个 + 2 * 2
 */
public class P1504_统计全1子矩形 {
    public int numSubmat(int[][] mat) {
        int[] height = new int[mat[0].length];
        int count = 0;
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[i].length; j++) {
                height[j] = (mat[i][j] == 0) ? 0 : (1 + height[j]);
            }
            // 一行高度的到，需要计算矩形啦
            count += largestNum(height);
        }
        return count;
    }
    public int largestNum(int[] h){
        int[] left = new int[h.length];
        int[] right = new int[h.length];
        int[] stack = new int[h.length];
        int index = -1;
        for (int i = 0; i < h.length; i++) {
            while (index != -1 && (h[stack[index]] >= h[i])){
                int cur = stack[index--];
                left[cur] = (index == -1)?-1:stack[index];
                right[cur] = i; // 重复的右边看到我就可以了。
            }
            stack[++index] = i;
        }
        while (index != -1){
            int cur = stack[index--];
            left[cur] = (index == -1)?-1:stack[index];
            right[cur] = h.length;
        }
        
        int count = 0;
        for (int i = 0; i < h.length; i++) {
            // height差 * 数量
            // 数量 = 项数 x （首项 + 末项）/2
            int hl = (left[i] == -1) ? 0 : h[left[i]];
            int hr = (right[i] == h.length) ? 0 : h[right[i]];
            count += (h[i]-Math.max(hl, hr)) * (right[i]-left[i]-1)*(right[i]-left[i])/2;
            //
        }
        System.out.println(count);
        return count;
    }
}
