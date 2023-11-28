package LeetCode;

import java.util.LinkedHashMap;
import java.util.Map;

public class P146_LRU缓存 {
    class LRUCache extends LinkedHashMap<Integer, Integer> {
        int mCapacity = 0;

        public LRUCache(int capacity) {
            super(capacity, 0.75f, true);
            mCapacity = capacity;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
            return size() > mCapacity;
        }

        public int get(int key) {
            return super.getOrDefault(key, -1);
        }

        public void put(int key, int value) {
            super.put(key, value);
        }
    }

}
