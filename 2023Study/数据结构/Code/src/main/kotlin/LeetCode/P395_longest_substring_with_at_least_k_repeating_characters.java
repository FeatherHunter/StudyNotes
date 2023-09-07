package LeetCode;

/**
 * 395. 至少有 K 个重复字符的最长子串
 * https://leetcode.cn/problems/longest-substring-with-at-least-k-repeating-characters/
 */
public class P395_longest_substring_with_at_least_k_repeating_characters {


    /**
     * 滑动窗口版本：
     * 1. 子串只有一种字符，>=K, 最长多长？
     * 2. 必须只有2，>=k
     * 26. 26种 >= k, 最长多长
     * 最终求Max。
     */
    public static int longestSubstring(String s, int k) {
        char[] arr = s.toCharArray();
        int max=0;
        for (int i = 1; i <= 26; i++) {
            int ans = getMaxOnlyKChar(arr, i, k);
            max = Math.max(max, ans);
        }
        return max;
    }

    /**
     * 求字符串src中，子串只有n种字符，每个次数K，最长多少
     */
    public static int getMaxOnlyKChar(char[] src, int n, int k){
        int[] help = new int[256];
        // 出现一个字符，负债为-1,
        // 当次数 >= k时，还完负债
        // count = 字符数量，大于K时，l移动
        int ans = 0;
        for (int l=0,r=0,count=0,debt=0;r<src.length;r++){
            help[src[r]]++;
            if(help[src[r]] == 1){
                count++;
            }
            if(help[src[r]] == k){
                debt++;
            }

            // 不符合要求时候，左边--
            while (count > n){
                if(help[src[l]] == 1){
                    count--;
                }
                if(help[src[l]] == k){
                    debt--;
                }
                help[src[l++]]--;
            }
            if(debt == n){
                ans = Math.max(ans, r - l + 1);
            }
        }
        return ans;
    }
}
