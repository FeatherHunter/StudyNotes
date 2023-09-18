package LeetCode;

/**
 * https://leetcode.cn/problems/merge-strings-alternately/
 */
public class P1768_交替合并字符串 {
    public String mergeAlternately(String word1, String word2) {
        StringBuilder builder = new StringBuilder(word1);
        char[] w2 = word2.toCharArray();
        int w1size = word1.length();
        // 0 1 2,插入在1，就是 0 【1】 2 【3】4 需要插在3
        // 0 1 长度为 2，能插入1次
        // 0 1 2 长度为3，插入2次
        for (int i = 0; i < w2.length; i++) {
            if(i < w1size - 1){ //
                builder.insert(1 + 2 * i, w2[i]);
            }else{
                // 已经插入完毕了就都在末尾了
                builder.append(w2[i]); // w2比较短的，走不到这个地方哦
            }
        }
        return builder.toString();
    }
}
