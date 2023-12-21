package LeetCode;

import java.util.HashMap;

// 双映射
public class P205_同构字符串_map {
    public boolean isIsomorphic(String so, String to) {
        char[] s = so.toCharArray();
        char[] t = to.toCharArray();
        if(s.length != t.length) return false;

        HashMap<Character, Character> map = new HashMap<>(); // s->t
        HashMap<Character, Character> map2 = new HashMap<>(); // t->s
        for (int i = 0; i < s.length; i++) {
            if(!map.containsKey(s[i])){
                map.put(s[i], t[i]); // 映射关系
            }else{
                // 包含的情况下
                if(map.get(s[i]) != t[i]){
                    return false; // 映射不对
                }
            }

            if(!map2.containsKey(t[i])){
                map2.put(t[i], s[i]); // 映射关系
            }else{
                if(map2.get(t[i]) != s[i]){ // 包含的情况下
                    return false; // 映射不对
                }
            }
        }
        return true;
    }
}
