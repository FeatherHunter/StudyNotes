package LeetCode;

/**
 * https://leetcode.cn/problems/reverse-vowels-of-a-string
 * 双指针
 */
public class P345_反转字符串中的元音字母 {
    public String reverseVowels(String s) {

        char[] a = s.toCharArray();
        int l = 0;
        int r = a.length - 1;
        while (l < r){
            while (l < r && !checkIsOk(a[l])){
                l++;
            }
            // 找到咯
            while (l < r && !checkIsOk(a[r])){
                r--;
            }
            if(l < r){
                swap(a, l, r);
            }
            l++;
            r--;
        }
        return new String(a);
    }
    public boolean checkIsOk(char c){
        return c=='a'||c=='A'||c=='e'||c=='E'||c=='i'||c=='I'||c=='o'||c=='O'||c=='u'||c=='U';
    }
    public void swap(char[] a, int i, int j){
        if(i == j) return;
        a[i] = (char) (a[i] ^ a[j]);
        a[j] = (char) (a[i] ^ a[j]);
        a[i] = (char) (a[i] ^ a[j]);
    }
}
