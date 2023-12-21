package LeetCode;

import java.util.ArrayList;
import java.util.List;

public class P17_电话号码的字母组合 {
    public List<String> letterCombinations(String digits) {
        return letterCombinations(digits.toCharArray(), 0);
    }

    public List<String> letterCombinations(char[] str, int i) {
        List<String> res = new ArrayList<>();
        if (i == str.length) return res;
        List<String> ans = getLetter(str[i]);
        List<String> rest = letterCombinations(str, i + 1);
        if(rest.size() < 1){
            return ans;
        }
        for (String an : ans) {
            for (String s : rest) {
                res.add(an + s);
            }
        }
        return res;
    }

    public List<String> getLetter(char num) {

        List<String> ans = new ArrayList<>();
        char c = 'a';
        int size = 3;
        switch (num) {
            case '2':
                c = 'a';
                break;
            case '3':
                c = 'd';
                break;
            case '4':
                c = 'g';
                break;
            case '5':
                c = 'j';
                break;
            case '6':
                c = 'm';
                break;
            case '7':
                c = 'p';
                size = 4;
                break;
            case '8':
                c = 't';
                break;
            case '9':
                c = 'w';
                size = 4;
                break;
        }
        for (int i = 0; i < size; i++) {
            ans.add(String.valueOf((char)(c+i)));
        }
        return ans;
    }
}
