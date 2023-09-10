package LeetCode;

import java.util.ArrayList;
import java.util.List;

public class P22_括号生成 {
    public static List<String> generateParenthesis(int n) {
        List<String> ans = new ArrayList<>();
        generate(n-1, n, ans, "(");
        return ans;
    }
    public static void generate(int lsize, int rsize, List<String> ans, String s){
        if(lsize == 0 && rsize == 0){
            ans.add(s);
            return;
        }else if(lsize == 0){
            generate(lsize, rsize - 1, ans,s + ")");
            return;
        }else if(rsize == 0){
            return;
        }

        if(lsize == rsize){
            generate(lsize - 1, rsize, ans, s + "(");
        }else{
            generate(lsize - 1, rsize, ans, s + "(");
            generate(lsize, rsize - 1, ans, s + ")");
        }
    }
}
