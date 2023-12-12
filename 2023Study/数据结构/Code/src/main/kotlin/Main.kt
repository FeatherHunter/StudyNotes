import DP.Code_02_Knight
import LeetCode.P315_计算右侧小于当前元素的个数
import LeetCode.P327_区间和的个数
import LeetCode.P6_N字形变换
import LeetCode.P992_subarrays_with_k_different_integers
import Study.美团_01_数组相同

fun main(args: Array<String>) {

    println(P327_区间和的个数.countRangeSum(intArrayOf(2147483647,-2147483648,-1,0), -1, 0))
}

fun study04() = {str:String, num:Int, lambda1:(String)->Unit, lambda2:(Int)->Boolean->
    lambda1(str)
    lambda2(num)
}

fun study02() = {lambda:(Int, Int) -> String, studyInfo: String ->
    lambda(1, 99)
}