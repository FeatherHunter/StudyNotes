package LeetCode;

public class P806_写字符串需要的行数 {
    public int[] numberOfLines(int[] widths, String s) {
        int[] ans = new int[2];
        int count = 1;
        int width = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if(width + widths[c-'a'] <= 100){
                width += widths[c-'a'];
            }else{
                count++;
                width = widths[c-'a'];
            }
        }
        ans[0] = count;
        ans[1] = width;
        return ans;
    }
}
