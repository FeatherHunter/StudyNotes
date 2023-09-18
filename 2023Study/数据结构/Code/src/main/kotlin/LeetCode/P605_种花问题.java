package LeetCode;

/**
 * https://leetcode.cn/problems/can-place-flowers/
 */
public class P605_种花问题 {
    public boolean canPlaceFlowers(int[] flowerbed, int n) {
        if(n == 0) return true;
        // 找到 左0，右0，本身0的数量是否为n
        for (int i = 0; i < flowerbed.length; i++) {
            if(flowerbed[i] == 0){
                if((i - 1 < 0) || flowerbed[i - 1] == 0){
                    if((i + 1 >= flowerbed.length) || flowerbed[i + 1] == 0){
                        n--; // 可以种花哦
                        flowerbed[i] = 1;
                    }
                }
            }
            if(n == 0){
                return true;
            }
        }
        return n == 0;
    }
}
