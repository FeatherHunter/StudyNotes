package LeetCode;

import java.util.Arrays;

/**
 * 2141. 同时运行 N 台电脑的最长时间
 * https://leetcode.cn/problems/maximum-running-time-of-n-computers/
 */
public class P2141_maximum_running_time_of_n_computers {
    /**
     * 时间范围，0~所有电池时间/n
     * 400时间，可以支持 >> n 个设备，说明可以支持更多时间。需要l=mid+1
     * < n个设备，说明时间多了，只能支持更少时间
     *
     * 时间复杂度O（n*log(sum/n)） 空间复杂度O（1）
     */
    public static long maxRunTime(int n, int[] batteries) {
        long l = 0;
        Arrays.sort(batteries);
        long sum = 0;
//        long max = 0;
        for (int battery : batteries) {
            sum += battery;
//            max = Math.max(max, battery);
        }
//        if(sum > max * n){ // 说明全是碎片时间
//            // 碎片时间都能满足最大时间 x n;
//            return sum / n;
//        }
//        long r = max; // 不是都是碎片时间，说明最大时间范围不会超过max
        long r = sum / n;
        long mid;
        long ans = 0;
        while (l <= r){
            mid = l + (r-l)/2;
            if(timeSupportMax(batteries, n, mid)){
                ans = mid;
                l = mid + 1;
            }else{
                r = mid - 1;
            }
        }
        return ans;
    }
    // time时间支持的最大设备数量
    public static boolean timeSupportMax(int[] batteries, int n, long time){
        long sum = 0;
        for (int battery : batteries) {
            if(battery >= time){
                n--;
            }else{
                sum += battery; // 9 / 2 = 4台
            }
            if(sum >= (long)n*time){
                return true; // 剩下电脑数量 x time > sum，够了
            }
        }
        return false; // 没有满足过
    }
    // 范围 1 ~ 血量/min毒
}
