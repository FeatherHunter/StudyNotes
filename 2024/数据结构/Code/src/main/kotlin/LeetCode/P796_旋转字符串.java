package LeetCode;

public class P796_旋转字符串 {
    public boolean rotateString(String s, String goal) {
        if(s.length() != goal.length()) return false;
        String pattern = goal + goal;
        // "abcde"
        // "cde[abcde]ab"
        for (int i = 0; i < goal.length(); i++) {
            if(s.charAt(0) == pattern.charAt(i)){
                if(s.equals(pattern.substring(i, i +  goal.length()))){
                    return true;
                }
            }
        }
        return false;
    }
}
