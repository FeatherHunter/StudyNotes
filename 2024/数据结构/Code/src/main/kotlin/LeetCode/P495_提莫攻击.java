package LeetCode;

public class P495_提莫攻击 {
    public int findPoisonedDuration(int[] timeSeries, int duration) {
        int ans = 0;
        for (int i = 1; i < timeSeries.length; i++) {
            if(timeSeries[i]-timeSeries[i-1] >= duration){
                ans += duration;
            }else{
                ans += timeSeries[i]-timeSeries[i-1];
            }
        }
        ans += duration;
        return ans;
    }
}
