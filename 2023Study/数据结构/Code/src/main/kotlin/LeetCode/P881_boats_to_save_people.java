package LeetCode;

import java.util.Arrays;
import java.util.Stack;

/**
 * https://leetcode.cn/problems/boats-to-save-people/
 */
public class P881_boats_to_save_people {
    public static int numRescueBoats(int[] people, int limit) {
        Arrays.sort(people);
        int count = 0;
        for (int l=0,r=people.length-1;l<=r;){
            if(people[l]+((l == r)?0:people[r])<=limit){
                l++;
            }
            r--;
            count++;
        }
        return count;
    }
}
