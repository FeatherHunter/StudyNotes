# LeetCode经验总结

[文本链接，点击这里↓](https://blog.csdn.net/feather_wch/article/details/132029690)

[toc]

|题号|题目|方法(同类型题目)||
|---|---|---|---|
|9|回文数|数学位运算、中间扩散、双指针||
|13|罗马数字转整数|左减右加||
|26|删除有序数组中的重复项|双指针(26、27)||
|27|移除元素|双指针||
|41|缺失的第一个正数|原地哈希||
|136|只出现一次的数|异或法(136、268)||
|268|丢失的数字|异或法(136、268)||

## 异或法

**适合场景**
找缺失数、找出现一次数

**题目**
268、136、287(不可以用异或法)

## 26.删除有序数组中的重复项

有序数组、双指针从左往右找不一样的数

## 41.原地哈希

哈希函数：限制常数级别额外空间
1映射到0,2映射到1，直至遍历整个数组。
解释：数值为i的映射到下标为i-1的位置
f(nums[i]) = nums[i] - 1
举例：nums[i] = 2
结果：f（2）= 2 - 1 = 1，下标为1


## 剑指 Offer 09. 用两个栈实现队列
### 栈的选择
Deque来替代：
```java
Deque<Integer> deque = new LinkedList<>();
Deque<Integer> deque = new ArrayDeque<>(); // 数组实现，性能更好
```
线程安全：
```java
ConcurrentLinkedDeque或者ArrayDeque
```


剑指 Offer 05. 替换空格 [Title](https://leetcode.cn/problems/ti-huan-kong-ge-lcof)
剑指 Offer 04. 二维数组中的查找 [Title](https://leetcode.cn/problems/er-wei-shu-zu-zhong-de-cha-zhao-lcof/description/)







## [494. 目标和](https://leetcode.cn/problems/target-sum/description/)
```java
// 1、递归版本
    public int findTargetSumWays(int[] nums, int target) {
        if(nums == null) return 0;
        return process(nums, 0, target);
    }
    public int process(int[] nums, int i, int target){
        if(i >= nums.length) return (target == 0) ? 1:0; // 只考虑BaseCase
        // + 和 - 两个方向找答案
        return process(nums, i + 1, target - nums[i]) + process(nums, i + 1, target + nums[i]);
    }
```
```java
// HashMap优化
```
缩小DP范围优化
```java
    public int findTargetSumWays(int[] nums, int target) {
        if(nums == null) return 0;
        int sum = 0;
        for (int i = 0; i < nums.length; i++) {
            nums[i] = Math.abs(nums[i]);
            sum += nums[i]; // 正数
        }
        target = Math.abs(target); // 正数
        if(target > sum) return 0;
        if((target + sum) % 2 != 0){
            // P的集合是小数，怎么可能
            return 0;
        }
        target = (target + sum) / 2;
        // i 0~1000，target 0~sum
        int[][] help = new int[1001][sum+1];
        help[nums.length][0] = 1;
        for (int i = nums.length - 1; i >= 0; i--) {
            for (int j = 0; j < sum + 1; j++) {
                int count = 0; // P是正的，只要相加为P就可以哦
                int num = nums[i];
                if(j - num >= 0){
                    count += help[i+1][j-num];
                }
                count += help[i+1][j];
                help[i][j] = count;
            }
        }
        return help[0][target];
    }
```
空间进一步优化
```java
// 二维DP空间优化
        int[] dp = new int[sum+1];
        dp[0] = 1;
        for (int num : nums) { //将i的循环，都变成数字num
            // 1. i这里就是j，就是target，dp[target] += dp[target-num] 
            // 2. dp[i] = dp[i] + dp[i-num] = 不使用num，上层的dp[i] + 使用num，上层的dp[i-num]
            for (int i = sum; i >= num; i--) { // 3. 为什么从sum开始？因为随着sum变小，条件可能越来越不符合
                dp[i] += dp[i - num]; // 
            }
        }
        return dp[target];
```
## [53. 最大子数组和](https://leetcode.cn/problems/maximum-subarray/description/)
```java
    public int maxSubArray(int[] nums) {
        int[] dp = new int[nums.length];
        dp[0] = nums[0];
        int max = dp[0];
        for (int i = 1; i < dp.length; i++) {
            // 当前位置结尾，和，要么是抛弃前面的和自己独立nums[i]，要么结合前面dp[i-1]+nums[i]
            dp[i] = (dp[i-1]<0)? nums[i]:(dp[i-1]+nums[i]);
            max = Math.max(max, dp[i]);
        }
        return max;
    }
// 空间优化
    public int maxSubArray(int[] nums) {
        int dp = nums[0];
        int max = dp;
        for (int i = 1; i < nums.length; i++) {
            // 当前位置结尾，和，要么是抛弃前面的和自己独立nums[i]，要么结合前面dp[i-1]+nums[i]
            dp = (dp < 0) ? nums[i]:(dp+nums[i]);
            max = Math.max(max, dp); // 新dp
        }
        return max;
    }
```
## [135. 分发糖果](https://leetcode.cn/problems/candy/description/)
**贪心+预处理**
```java
    public int candy(int[] ratings) {
        // 1、左坡
        int[] left = new int[ratings.length];
        left[0] = 1;
        for (int i = 1; i < ratings.length; i++) {
            if(ratings[i-1] < ratings[i]){ // 1. 比左边大 +1
                left[i] = left[i-1] + 1;
            }else{
                left[i] = 1;
            }
        }
        // 2、右坡
        int[] right = new int[ratings.length];
        right[ratings.length - 1] = 1;
        for (int i = ratings.length - 2; i >= 0; i--) {
            if(ratings[i] > ratings[i+1]){ // 1. 比左边大 +1
                right[i] = right[i+1] + 1;
            }else{
                right[i] = 1;
            }
        }
        // 3、取最大值
        int sum = 0;
        for (int i = 0; i < ratings.length; i++) {
            sum += Math.max(left[i], right[i]);
        }
        return sum;
    }
```
## [97. 交错字符串](https://leetcode.cn/problems/interleaving-string/)
```java
    public static boolean isInterleave(String s1, String s2, String s3) {
        if(s3.length() != s1.length() + s2.length()) return false;
        // dp[i][j] s1拿前i个字符，s2拿前j个字符，构成s3前(i+j)个字符
        boolean[][] dp = new boolean[s1.length() + 1][s2.length() + 1];
        for (int i = 0; i < dp.length; i++) {
            for (int j = 0; j < dp[i].length; j++) {
                if(i == 0){
                    if(j == 0){
                        dp[0][0] = true;
                    }else{ // 没有s1
                        dp[0][j] = dp[0][j-1] && s2.charAt(j-1) == s3.charAt(j-1);
                    }
                }else{
                    if(j == 0){ // 没有s2
                        dp[i][0] = dp[i-1][0] && s1.charAt(i-1) == s3.charAt(i-1);
                    }else{
                        // 需要考虑的是从上，从左，两种情况，满足一个就可以
                        dp[i][j] = (dp[i-1][j] && (s1.charAt(i-1)==s3.charAt(i+j-1)))
                                || (dp[i][j-1] && (s2.charAt(j-1)==s3.charAt(i+j-1)));
                    }
                }
            }
        }
        return dp[s1.length()][s2.length()];
    }
```
## [72. 编辑距离](https://leetcode.cn/problems/edit-distance/description/)
**编辑距离场景**
判断要搜索的词语和热搜的关联性，同步给热搜信息。
```java
    public int minDistance(String word1, String word2) {
        return minDistance(word1, word2, 1, 1, 1);
    }
    /**
     * 样本对应模型：根据结尾处，判断要怎么划分可能性。
     */
    public int minDistance(String word1, String word2, int ac, int dc, int rc) {
        // 1、basecase两种
        int N = word1.length() + 1;
        int M = word2.length() + 1;
        char[] s1= word1.toCharArray();
        char[] s2= word2.toCharArray();
        int[][] dp = new int[N][M];
        for (int i = 0; i < M; i++) {
            dp[0][i] = ac * i; // 添加操作
        }
        for (int i = 0; i < N; i++) {
            dp[i][0] = dc * i; // 删除操作
        }
        for (int i = 1; i < N; i++) {
            for (int j = 1; j < M; j++) {
                // 2、普通情况
                //  1.1 dp[i-1][j-1]     【最后一个字符相同】操作：只需要变前面
                //  1.2 dp[i-1][j-1] + r 【最后一个字符不同】操作：变前面，再替换
                //  2. dp[i-1][j] + d 操作：将[i-1]（自己的前面）变成目标j，再删除字符
                //  3. dp[i][j-1] + a 操作：将i自己变成[j-1](目标的前部分)，再添加字符
                // 三种找最小
                // 1.1
                if (s1[i - 1] == s2[j - 1]){ // 字符串下标-1
                    dp[i][j] = dp[i-1][j-1]; // 改变
                }else{ // 1.2
                    dp[i][j] = dp[i-1][j-1] + rc; // 改变 + 替换
                }
                dp[i][j] = Math.min(dp[i][j], dp[i-1][j] + dc);// 2.删除
                dp[i][j] = Math.min(dp[i][j], dp[i][j-1] + ac);// 3.增加
            }
        }
        return dp[word1.length()][word2.length()];
    }
```
## 套路：括号嵌套
任何括号嵌套套路：
1. x
```java
int[] f(i) // 0~i算到右侧，或者第一个括号
r[0] = 计算的数值
r[1] = 右侧下标
```
### [224. 基本计算器](https://leetcode.cn/problems/basic-calculator/)
```java
    public static int calculate(String s) {
        char[] num = s.toCharArray();
        int[] r = cal(num, 0);
        return r[0]; // result
    }
    /**
     * @return r[0]结果 r[1]终止位置
     */
    public static int[] cal(char[] s, int i){
        LinkedList<String> que = new LinkedList<>();
        int num = 0; // 用于计算数值
        while (i < s.length && s[i] != ')'){
            if(s[i] == ' '){
                i++;
                continue; // 无视空格
            }
            if(s[i] >= '0' && s[i] <= '9'){
                num = num * 10 + s[i++] - '0';
            }else if(s[i] != '('){ // 遇到运算符
                addNum(que, num); // 将之前的数字，添加进去
                que.addLast(String.valueOf(s[i++])); // 运算符进入队列
                num=0;
            }else{
                // 遇到左括号，直接拿去计算
                int r[] = cal(s, i + 1);
                num = r[0]; // 获得返回值
                i = r[1] + 1; // i跳转到下标
            }
        }
        addNum(que, num); //最后一个数字加入
        return new int[]{getNum(que), i}; //计算出结果， 末尾位置
    }
    private static void addNum(LinkedList<String> que, int num) {
        if(!que.isEmpty()){
            String top = que.pollLast();
            if(top.equals("*")){
                int cur = Integer.valueOf(que.pollLast());
                num = cur * num;
            }else if(top.equals("/")){
                int cur = Integer.valueOf(que.pollLast());
                num = cur / num;
            }else{
                que.addLast(top);
            }
        }
        que.addLast(String.valueOf(num));
    }
    private static int getNum(LinkedList<String> que) {
        int sum = 0;
        while (que.size() > 1){
            int a = Integer.parseInt(que.removeFirst());
            String b = que.removeFirst();
            int c = Integer.parseInt(que.removeFirst());
            if(b.equals("+")){
                sum = (a + c);
            }else{
                sum = (a - c);
            }
            que.addFirst(String.valueOf(sum));
        }
        return Integer.parseInt(que.removeFirst());
    }
```
