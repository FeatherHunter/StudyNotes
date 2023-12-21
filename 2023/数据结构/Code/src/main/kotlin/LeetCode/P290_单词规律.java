package LeetCode;

import java.util.HashMap;

public class P290_单词规律 {

    public boolean wordPattern(String pattern, String s) {

        // 要考虑双map
        String[] a = s.split(" ");
        HashMap<Character, String> map = new HashMap<>(); // p-s
        HashMap<String, Character> map2 = new HashMap<>(); // s-p
        char[] p = pattern.toCharArray();
        if (p.length != a.length) return false;
        for (int i = 0; i < p.length; i++) {
            if(map.containsKey(p[i])){
                if(!map.get(p[i]).equals(a[i])){
                    return false;
                }
            }else{
                map.put(p[i], a[i]);
            }

            if(map2.containsKey(a[i])){
                if(!map2.get(a[i]).equals(p[i])){
                    return false;
                }
            }else{
                map2.put(a[i], p[i]);
            }
        }
        return true;
    }
}
