package LeetCode;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class P139_单词拆分 {
    /**
     * 进行懒缓存：
     * 1. 怎么改进？所有的写法，都要进行懒缓存
     */
    public static boolean wordBreak(String s, List<String> wordDict) {

        if(s == null || s.length() == 0) return false;
        if (wordDict == null || wordDict.size() == 0) return false;
        wordDict.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o2.length() - o1.length();// 先从大到小开始找
            }
        });

        return wordBreak(s.toCharArray(), 0, wordDict, new HashMap<>());
    }

    public static boolean wordBreak(char[] w, int i, List<String> wordDict, HashMap<Integer, Boolean> map){
        if(map.containsKey(i)){
            return map.get(i);
        }
        if(i >= w.length){
            return true;
        }
        for (String s : wordDict) {
            if(w.length - i < s.length()) continue;//// 先从大到小开始找,到合适的再匹配

            if(isSame(w, i, s.toCharArray())){
                boolean r = wordBreak(w, i + s.length(), wordDict, map);
                if(r){
                    map.put(i, true);
                    return true;
                }
            }
        }
// 没找到一个一样的
        map.put(i, false);
        return false;
    }

    public static boolean isSame(char[] w, int i, char[] temp){
        for (int j = 0; j < temp.length; j++) {
            if(w[i + j] != temp[j]){
                return false;
            }
        }
        return true;
    }

}
