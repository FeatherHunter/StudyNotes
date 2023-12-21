package LeetCode;

/**
 * 1234. 替换子串得到平衡字符串
 * https://leetcode.cn/problems/replace-the-substring-for-balanced-string/
 * 时间 8ms 击败 63.48%使用 Java 的用户
 * */
public class P1234_replace_the_substring_for_balanced_string {
    public static int balancedString(String s) {
        char[] arr = s.toCharArray();
        int[] counts = new int[4];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = (char) switchToInt(arr[i]);
            counts[arr[i]]++; // 计算出每种字符的数量
        }
        int N = arr.length;
        int target = arr.length / 4; // 目标
        int min = N;

        for(int l=0,r=0;l<N;l++){
            while(!isOk(counts,r-l,target) && r < N){ // 不符合要求就需要移动右侧
                counts[arr[r++]]--;
            }
            if(isOk(counts,r-l,target)){
                min = Math.min(min, r-l);
            }
            counts[arr[l]]++;
        }

        return min;
    }

    // 判断当前【自由窗口】的长度要求下，能否符合变换需求
    public static boolean isOk(int[] counts, int len, int target){
        for (int i = 0; i < counts.length; i++) {
            if(counts[i] > target){
                return false;
            }
            len -= target - counts[i];
        }
        return len == 0;
    }

    public static int switchToInt(char c){
        if (c == 'Q') return 0;
        if (c == 'W') return 1;
        if (c == 'E') return 2;
        if (c == 'R') return 3;
        return -1;
    }
}
