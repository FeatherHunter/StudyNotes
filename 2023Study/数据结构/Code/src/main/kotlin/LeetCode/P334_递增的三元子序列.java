package LeetCode;

import java.util.Arrays;

/**
 * https://leetcode.cn/problems/increasing-triplet-subsequence
// * [单调栈] 🥹🥹🥹🥹🥹封存啦
// * 递增：大压小，如果出现栈size=3代表OK。但是会错过
// * 递减：会找到遗漏的数据
// * 【单调栈】二：构造右侧最大数组，遍历原数组，找到辅助数组中是否可以连续起码两个数据。也不可以哦 [0,4,1,-1,2] 0->1->-1不可以
 * 贪心策略：
 */
public class P334_递增的三元子序列 {


    // 尝试二
//    // [0,4,1,-1,2]
//    // [1,-1,4,4,-1] // 0->1->-1 XXXX
//
//    // [2,1,5,0,4,6]
//    // [2,2,5,4,5,-1] // 右侧最大数字 0->2->5
//
//    // [20,100,10,12,5,13]
//    // [1,-1,3,5,5,-1] // 0->1->-1 不可以哦 2->3->5
//    static int stack[] = new int[500001];
//    int top = 0;
//    public boolean increasingTriplet(int[] nums) {
//        top = 0;
//        int[] max = new int[nums.length];
//        Arrays.fill(max, -1);
//
//        // 小压大
//        for (int i = 0; i < nums.length; i++) {
//            while (top > 0 && nums[stack[top-1]] <= nums[i]){ // top-1需要注意
//                max[stack[top-1]] = i; // i是栈顶，右侧最大或者等于的数据
//                top--; //
//            }
//            stack[top++] = i;
//        }
//        for (int i = max.length - 1; i >= 0; i--) {
//            if(max[i] != -1 && nums[i] == nums[max[i]] ){
//                // 数值相同
//                max[i] = max[max[i]];
//            }
//        }
//        System.out.println(Arrays.toString(max));
//
//        // 遍历max，尝试找到两次合法数据
//        for (int i = 0, index, count; i < max.length; i++) {
//            index = i;
//            count = 0;
//            while (max[index] != -1){
//                count++;
//                index = max[index];
//                if(count >= 2){
//                    return true;
//                }
//            }
//        }
//        // 没有找到哦
//        return false;
//    }
    // 尝试一
//    static int stack[] = new int[500001];
//    int top = 0;
//    public boolean increasingTriplet(int[] nums) {
//        top = 0;
//        for (int i = 0; i < nums.length; i++) {
//            while (top > 0 && nums[stack[top-1]] >= nums[i]){ // top-1需要注意
//                top--; // 需要保持大压小，但是此时，栈的数量会降低哦。
//            }
//            stack[top++] = i;
//            // 加入元素后，看看栈的size
//            if(top == 3){
//                return true;
//            }
//        }
//        top = 0;
//        // 递减栈
//        for (int i = nums.length - 1; i >= 0; i--) {
//            while (top > 0 && nums[stack[top-1]] <= nums[i]){ // top-1需要注意,小压大。
//                top--;
//            }
//            stack[top++] = i;
//            if(top == 3){
//                return true;
//            }
//        }
//        // 单调递增，单调递减，都没找到哦
//        return false;
//    }
}
