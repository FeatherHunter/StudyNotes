package LeetCode;

public class P520_检测大写字母 {
    public boolean detectCapitalUse(String word) {
        if(word.length() == 1) return true;
        boolean isUp = false;
        if(word.charAt(0) - 'A' < 26){
            if(word.charAt(1) - 'A' < 26){
                isUp = true; //
            }
        }else{
            // 后面都需要是小写
            if(word.charAt(1) - 'A' < 26){
                return false;
            }
        }
        System.out.println(isUp);
        for (int i = 2; i < word.length(); i++) {
            char c = word.charAt(i);
            System.out.println(c);
            System.out.println(c-'a');
            // 大写字母，但是要求是小写
            if(!isUp && c - 'a' < 0){
                return false;
            }else if (isUp && c - 'a' >= 0){
                return false;
            }
        }
        return true;
    }
}
