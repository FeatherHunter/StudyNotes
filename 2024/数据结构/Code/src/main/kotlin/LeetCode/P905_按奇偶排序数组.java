package LeetCode;

public class P905_按奇偶排序数组 {
    public int[] sortArrayByParity(int[] nums) {
        int l = -1;
        int r = nums.length - 1;
        for (int i = 0; i < r; i++) {
            // ji数
            if(nums[i] % 2 != 0){
                int temp = nums[r];
                nums[r] = nums[i];
                nums[i] = temp;
                r--;
                i--;
            }else{
//                int temp = nums[l];
//                nums[l] = nums[i];
//                nums[i] = temp;
//                l++;
            }
            // 偶数
        }
        return nums;
    }
}
