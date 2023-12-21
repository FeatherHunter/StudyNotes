package LeetCode;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class P242_有效的字母异位词 {
    public boolean isAnagram(String s, String t) {
        if(s.length() != t.length()) return false;

        HashMap<Character, Integer> map1 = new LinkedHashMap<>();
        HashMap<Character, Integer> map2 = new LinkedHashMap<>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if(map1.containsKey(c)){
                map1.put(c, map1.get(c) + 1); // ++
            }else{
                map1.put(c, 1); // 第一次放入
            }
        }
        for (int i = 0; i < t.length(); i++) {
            char c = t.charAt(i);
            if(map2.containsKey(c)){
                map2.put(c, map2.get(c) + 1); // ++
            }else{
                map2.put(c, 1); // 第一次放入
            }
        }

        if(map1.size() != map2.size()) return false;
        for(Map.Entry<Character, Integer> entry: map1.entrySet()){
            if(map2.containsKey(entry.getKey())){
                // 需要考虑Integer可能认为是不同的，需要intValue比较
                if(map2.get(entry.getKey()).intValue() != entry.getValue().intValue()){
                    return false; // 数量不一样
                }
            }else{
                // 不包含 一定不对应
                return false;
            }
        }

        return true;
    }
}
