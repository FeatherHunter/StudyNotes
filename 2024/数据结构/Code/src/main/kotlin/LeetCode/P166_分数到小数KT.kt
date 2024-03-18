package LeetCode

import kotlin.math.abs

class P166_分数到小数KT {

    fun fractionToDecimal(numerator: Int, denominator: Int): String {
        return fractionToDecimal(numerator.toLong(), denominator.toLong())
    }

    fun fractionToDecimal(numerator: Long, denominator: Long): String {
        var res = (numerator / denominator).toString()
        if(res == "0"){
            if(numerator < 0 && denominator > 0){
                res = "-0"
            }else if(numerator > 0 && denominator < 0){
                res = "-0"
            }
        }
        println(res)
        var left = abs(numerator.toLong() % denominator)
        var d = abs(denominator)
        if(left != 0L) res += "."
        val map = HashMap<Long, Int>() // 余数
        var temp = ""
        // 1 / 6 余1  ===> 4/6 ===>
        // 10/6 1 余4
        // 40/6 6 余4
        while (left != 0L){
            if(map.contains(left)){
                // 找到数据咯
                val startIndex = map.getOrDefault(left, 1)
                temp = "${temp.substring(0, startIndex)}(${temp.substring(startIndex)})"
                break
            }else{
                map.put(left, temp.length) // 有这个余数
            }
            // 一开始的4搞出来哦
            left *= 10
            val n = left / d
            left %= d // 余数4->40->400
            temp += n // 加上这个结果 0

        }
        res += temp
        return res
    }
}