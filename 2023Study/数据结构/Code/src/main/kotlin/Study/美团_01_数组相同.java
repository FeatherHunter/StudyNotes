package Study;

/**
 * 两个数组内容完全一致，打散后，数组A和数组B长度分变为n和m。需要让两个数组完全一致，可以做两个操作：
 * 1. 数组中删除某个数a，消耗时间|a|
 * 2. 数组中一个数a变成数b，消耗时间|a-b|
 * 求解：最少多少时间可以让两个数列再次相同。
 *
 * 输入描述：
 * 第一行两个空格隔开，正整数，n和m
 * 第二行，n个整数
 * 第三行，m个整数
 * 1 <= n,m <= 2000
 * |Ai||Bi| <= 10000
 */
public class 美团_01_数组相同 {

    public static int min(int[] a, int[] b){
        return zuo(a, 0, b, 0); // 从0开始一致。
    }

    /**
     * 左函数：数组A [ai...] 和 数组B [bi...]
     * 两个数组从ai和bi开始，完全一样
     * 返回：最小消耗的时间
     */
    public static int zuo(int[] A, int ai, int[] B, int bi){
        // 第一步：Base Case，到头了
        if(ai == A.length && bi == B.length){
            return 0; // 两个都到头了
        }
        if(ai == A.length){
            return B[bi] // B一定要删掉一个
                    + zuo(A, ai, B, bi +1); // 后续也需要删除
        }else if(bi == B.length){
            return A[ai] + zuo(A, ai + 1, B, bi);
        }

        // 说明A和B都还有数据
        int p1 = A[ai] + zuo(A, ai + 1, B, bi); // 删除A
        int p2 = B[bi] + zuo(A, ai, B, bi + 1); // 删除B
//        int p3 = A[ai] + B[bi] + zuo(A, ai + 1, B, bi + 1); // 都删除 // 该情况会被p1和p2包含
        int p4 = Math.abs(A[ai] - B[ai]) + zuo(A, ai + 1, B, bi + 1); //都改变,以及AI和BI都一样，也包括在这个可能中
        // 我们的递归中限制，只能改同位置的数。是可以做到错位，然后符合条件的。比如前面删除了一部分元素，后面可以两个数对齐。
        return Math.min(Math.min(p1, p2), p4);

    }
}
