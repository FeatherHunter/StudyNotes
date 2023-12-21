package LeetCode;

public class P1456_定长子串中元音的最大数目_滑动窗口 {
    public int maxVowels(String s, int k) {
        char[] a = s.toCharArray();
        int l = 0;
        int r = 0; // r指向检查过的字符char的下一个位置
        int count = 0;
        // 准备好基本长度 // 1、先到 k-1 的窗口大小
        while (r < k - 1) {
            if (isVowel(a[r++])) {
                count++;
            }
        }
        // 2、开始滑动（k长度）
        int max = 0;
        while (r < a.length) {
            // 右边滑动
            if (isVowel(a[r++])) {
                count++;
            }
            max = Math.max(max, count); // 获取当前最大值
            // 左边滑动
            if (isVowel(a[l++])) {
                count--;
            }
        }
        return max;
    }

    public boolean isVowel(char c) {
        return c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u';
    }

}
