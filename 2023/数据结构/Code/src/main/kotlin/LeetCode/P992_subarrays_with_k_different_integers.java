package LeetCode;

import java.util.HashMap;
import java.util.HashSet;

/**
 *  992. K 个不同整数的子数组
 * https://leetcode.cn/problems/subarrays-with-k-different-integers/
 * */
public class P992_subarrays_with_k_different_integers {

    /**
     * 吸收思路。++arr[xxx] == 1 代表有新增的种类，剩下就是单纯的++arr[xxx]增加数量。
     * 类似HashSet的效果
     * O(n)的效率
     * */
    public static int subarraysWithKDistinct2(int[] nums, int k) {
        return numsOfMostKinds(nums, k)-numsOfMostKinds(nums,k-1);
    }

    public static int numsOfMostKinds(int[] arr, int k){
        int[] counts = new int[20001];
        int n = arr.length;
        int ans = 0;
        for (int l=0,r=0,collect=0;r<n;r++) {
            if(++counts[arr[r]] == 1) {
                collect++;
            }
            while(collect > k) {
                if(--counts[arr[l++]] == 0) {
                    collect--;
                }
            }
            ans += (r-l+1); // 以r为终点，新增的子数组有哪些
        }
        return ans;
    }



    /**
     * @author 1.0 自己版本，用了HashMap，滑动窗口的扩大、移动、以及缩小
     * 时间太久远了。
     */
    public static int subarraysWithKDistinct(int[] nums, int k) {

        HashMap<Integer, Integer> map  = new HashMap<>();
        int n = nums.length;
        int count = 0;
        for (int l=0,r=0;l<n;l++){
            // 窗口收缩
            while(r-l > k){
                int num = nums[--r];
                mapSub(map, num);
            }

            // r 先移动到符合要求的地方
            while(!isOk(k, map) && r < n){
                int num = nums[r++];
                // 加入数字
                mapAdd(map, num);
            }
            // 此时一定是Ok的
            while(isOk(k, map) && r < n){
                // 既然OK，统计一下不过分吧？
                count++;
                // 此时已经OK
                int num = nums[r++];
                mapAdd(map, num);
            }
            if(isOk(k, map)){
                count++;
            }

            // 此时不OK或者到头了就要删除左侧一个
            int num = nums[l];
            mapSub(map, num);
        }
        return count;
    }

    static void mapAdd(HashMap<Integer, Integer> map, int num){
        if(map.containsKey(num)){
            map.put(num,map.get(num)+1);
        }else{
            map.put(num,1);
        }
    }
    static void mapSub(HashMap<Integer, Integer> map, int num){
        if(map.containsKey(num)){
            int c = map.get(num) - 1;
            if(c <= 0){
                map.remove(num);
            }else{
                map.put(num,c);
            }
        }
    }

    static boolean isOk(int k, HashMap<Integer, Integer> map){
        if(map.size() == k){
            return true;
        }else {
            return false;
        }
    }
}
