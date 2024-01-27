package LeetCode;

import java.util.ArrayList;
import java.util.HashSet;

public class P500_键盘行 {
    public String[] findWords(String[] words) {
        HashSet<Character> s1 = new HashSet<>();
        s1.add('Q');
        s1.add('W');
        s1.add('E');
        s1.add('R');
        s1.add('T');
        s1.add('Y');
        s1.add('U');
        s1.add('I');
        s1.add('O');
        s1.add('P');

        HashSet<Character> s2 = new HashSet<>();
        s2.add('A');
        s2.add('S');
        s2.add('D');
        s2.add('F');
        s2.add('G');
        s2.add('H');
        s2.add('J');
        s2.add('K');
        s2.add('L');

        HashSet<Character> s3 = new HashSet<>();
        s3.add('Z');
        s3.add('X');
        s3.add('C');
        s3.add('V');
        s3.add('B');
        s3.add('N');
        s3.add('M');

        ArrayList<String> ans = new ArrayList<>();

        for (String word : words) {
            char[] a = word.toUpperCase().toCharArray();
            HashSet<Character> set = null;
            if(s1.contains(a[0])){
                set = s1;
            }else if(s2.contains(a[0])){
                set = s2;
            }else{
                set = s3;

            }
            boolean ok = true;
            for (int i = 1; i < a.length; i++) {
                if(!set.contains(a[i])){
                    ok = false;
                    break;
                }
            }
            if(ok){
                ans.add(word);
            }
        }
        String[] res = new String[ans.size()];
        for (int i = 0; i < ans.size(); i++) {
            res[i] = ans.get(i);
        }
        return res;
    }
}
