package LeetCode;

import java.util.ArrayList;
import java.util.List;

/**
 * https://leetcode.cn/problems/kids-with-the-greatest-number-of-candies
 * 默认思路：遍历
 * 牛逼思路：
 */
public class P1431_拥有最多糖果的孩子 {
    public List<Boolean> kidsWithCandies(int[] candies, int extraCandies) {
        int max = candies[0];
        for (int i = 0; i < candies.length; i++) {
            max = Math.max(max, candies[i]);
        }
        List<Boolean> ans = new ArrayList<>();
        for (int i = 0; i < candies.length; i++) {
            ans.add((candies[i] + extraCandies) >= max ? true : false);
        }
        return ans;
    }
}
