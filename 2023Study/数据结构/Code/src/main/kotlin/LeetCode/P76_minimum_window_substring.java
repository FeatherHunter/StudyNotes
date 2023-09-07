package LeetCode;


/**
 * 76. 最小覆盖子串
 * https://leetcode.cn/problems/minimum-window-substring/
 */
public class P76_minimum_window_substring {
    public String minWindow(String s, String t) {

        if(s == null || t == null || s.length() < t.length()){
            return "";
        }
        char[] src = s.toCharArray();
        char[] ta = t.toCharArray();
        int[] cnts = new int[256];
        for (char c : ta) {
            cnts[c]--;
        }
        int len = Integer.MAX_VALUE;
        int start=0;
        for(int l=0,r=0,debt=ta.length;r<src.length;r++){
            if(cnts[src[r]]++ < 0){// 《 0 说明在还债
                debt--;
            }
            if(debt==0){
                while (cnts[src[l]] > 0){
                    cnts[src[l]]--;
                    l++;//还债时缩短左侧边距
                }
                if(len > (r-l+1)){
                    start = l;
                    len = r-l+1;
                }
            }
        }
        return (len==Integer.MAX_VALUE)?"":s.substring(start,start+len);
    }
}
