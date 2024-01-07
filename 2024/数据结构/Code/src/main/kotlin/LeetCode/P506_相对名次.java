package LeetCode;

import java.util.Arrays;
import java.util.Comparator;

public class P506_相对名次 {
    public String[] findRelativeRanks(int[] score) {
        String[] ans = new String[score.length];
        int[][] arr = new int[score.length][2];
        for (int i = 0; i < score.length; i++) {
            arr[i][0] = score[i];
            arr[i][1] = i;
        }
        Arrays.sort(arr, (a,b)->b[0]-a[0]);
        for (int i = 0; i < score.length; i++) {
            if(i == 0){
                ans[arr[i][1]] = "Gold Medal";
            }else if(i == 1){
                ans[arr[i][1]] = "Silver Medal";
            }else if(i == 2){
                ans[arr[i][1]] = "Bronze Medal";
            }else{
                ans[arr[i][1]] = (i + 1) + "";
            }
        }
        return ans;
    }
}
