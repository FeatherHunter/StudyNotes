package DisjointSetUnion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UnionSet<T> {
    HashMap<T, Set<T>> setMap;
    HashMap<Set<T>, Integer> sizeMap;
    HashMap<Set<T>, Set<T>> parentMap;

    public UnionSet(){
        setMap = new HashMap<>();
        sizeMap = new HashMap<>();
        parentMap = new HashMap<>();
    }

    public int size(){
        return sizeMap.size();
    }

    public void insert(T v){
        if(!setMap.containsKey(v)){
            Set s = new Set<>(v);
            setMap.put(v, s);
            sizeMap.put(s, 1);
            parentMap.put(s, s);
        }
    }

    public static class Set<T>{
        T value;

        public Set(T value) {
            this.value = value;
        }
    }
    private Set findParent(T v){
        Set x = setMap.get(v);
        List<Set> list = new ArrayList<>();
        while (x != parentMap.get(x)){
            list.add(x); // 缓存当前的
            x = parentMap.get(x); // 找到父类
        }
        for (Set set : list) {
            parentMap.put(set, x); // 父亲都是找到的节点
        }

        return x; // 找到啦
    }

    public boolean isSameSet(T a, T b){
        return findParent(a) == findParent(b);
    }

    public boolean union(T a, T b){
        if(isSameSet(a, b)){
            return false; // 合并失败
        }
        Set s1 = findParent(a);
        Set s2 = findParent(b);
        if(sizeMap.get(s1) >= sizeMap.get(s2)){
            parentMap.put(s2, s1);
            sizeMap.put(s1, sizeMap.get(s1) + sizeMap.get(s2));
            sizeMap.remove(s2); // s2没有了,--
        }else{
            parentMap.put(s1, s2);
            sizeMap.put(s2, sizeMap.get(s1) + sizeMap.get(s2));
            sizeMap.remove(s1); // s1没有了,--
        }
        return true;
    }
}
