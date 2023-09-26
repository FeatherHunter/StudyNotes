package LeetCode;

import java.util.HashMap;
import java.util.Random;

public class P380_O1时间插入删除和获取随机元素 {
    class RandomizedSet {
        HashMap<Integer, Integer> indexMap = new HashMap<>(); // value-index
        HashMap<Integer, Integer> valueMap = new HashMap<>(); // index-value
        int size = 0;
        Random random = new Random();

        public RandomizedSet() {
        }

        public boolean insert(int val) {
            if (indexMap.containsKey(val)) {
                return false;
            }
            indexMap.put(val, size);
            valueMap.put(size, val);
            size++;
            return true;
        }

        public boolean remove(int val) {
            if (indexMap.containsKey(val)) {
                size--;
                // 查询到该数值
                int oldindex = indexMap.get(val);
                int newValue = valueMap.get(size); // 末尾数值
                valueMap.put(oldindex, newValue); // 覆盖新值
                valueMap.remove(size);// 删除最后元素 // 包含了只有一个元素的情况
                indexMap.put(newValue, oldindex); // 更新新值的位置
                indexMap.remove(val); // 避免只有一个元素，直接删除
                return true;
            }
            return false;
        }

        // 获取随机值
        public int getRandom() {
            int index = random.nextInt(size);
            return valueMap.get(index);
        }
    }

}
