package LeetCode;

/**
 * 自我评价：思路明确
 * 9ms 击败 96.56%使用 Java 的用户
 */
public class P1438_绝对差不超过限制的最长连续子数组 {
    // 两个单调队列。
    static int MAIN = 100001;
    static int[] MaxQueue = new int[MAIN];
    static int[] MinQueue = new int[MAIN];
    static int maxh = 0;
    static int minh = 0;
    static int maxt = 0;
    static int mint = 0;
    public static int longestSubarray(int[] nums, int limit) {
        maxh = maxt = minh = mint = 0;
        int ans = 0;
        for (int l = 0, r = 0; r < nums.length; r++) {
            while (maxh < maxt && (nums[MaxQueue[maxt - 1]] <= nums[r])){
                maxt--;
            }
            MaxQueue[maxt++] = r;
            while (minh < mint && (nums[MinQueue[mint - 1]] >= nums[r])){
                mint--;
            }
            MinQueue[mint++] = r;
            while (maxh < maxt && minh < mint && nums[MaxQueue[maxh]] - nums[MinQueue[minh]] > limit){
                l = Math.min(MaxQueue[maxh], MinQueue[minh]);
                if(MaxQueue[maxh] < MinQueue[minh]){
                    maxh++;
                }else if(MaxQueue[maxh] > MinQueue[minh]){
                    minh++;
                }else{
                    maxh++;
                    minh++;
                }
                l++; // l走到可能符合的下一位
            }
            ans = Math.max(ans, r - l + 1);
        }
        return ans;
    }
}
