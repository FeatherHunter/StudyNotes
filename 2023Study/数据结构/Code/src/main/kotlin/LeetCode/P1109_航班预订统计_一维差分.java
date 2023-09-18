package LeetCode;

public class P1109_航班预订统计_一维差分 {
    /**
     * 这里有 n 个航班，它们分别从 1 到 n 进行编号。
     * 有一份航班预订表 bookings ，表中第 i 条预订记录 bookings[i] = [firsti, lasti, seatsi] 意味着在从 firsti 到 lasti （包含 firsti 和 lasti ）的 每个航班 上预订了 seatsi 个座位。
     * 请你返回一个长度为 n 的数组 answer，里面的元素是每个航班预定的座位总数。
     */
    public int[] corpFlightBookings(int[][] bookings, int n) {
        int[] arr = new int[n+2];
        for (int i = 0; i < bookings.length; i++) {
            arr[bookings[i][0]] += bookings[i][2];
            arr[bookings[i][1] + 1] -= bookings[i][2];
        }
        int[] ans = new int[n];
        ans[0] = arr[1];
        for (int i = 1; i < ans.length; i++) {
            ans[i] = ans[i-1] + arr[i+1];
        }
        return ans;
    }
}
