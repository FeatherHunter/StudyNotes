package LeetCode;

/**
 * 2ms 击败 95.70%使用 Java 的用户
 */
public class P125_验证回文串 {

    public boolean isPalindrome(String sold) {
        char[] s = sold.toLowerCase().toCharArray();
        char[] n = new char[sold.length()]; // 放新字符串
        int index = 0;
        for (int i = 0; i < s.length; i++) {
            char c = s[i];
            if((c <= 'z' && c >= 'a') || (c <= '9' && c >= '0')){
                n[index++] = c;
            }
        }
        int l = 0;
        int r = index - 1;
        while (l < r){
            if(n[l] != n[r]){
                return false;
            }
            l++;
            r--;
        }
        return true; // 走到这里一定是符合要求的
    }
}
