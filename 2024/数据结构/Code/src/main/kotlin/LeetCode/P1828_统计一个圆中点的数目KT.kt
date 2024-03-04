package LeetCode

import java.lang.Math.pow
import kotlin.math.sqrt

class P1828_统计一个圆中点的数目KT {
    fun countPoints(points: Array<IntArray>, queries: Array<IntArray>): IntArray {
        val res = IntArray(queries.size)
        queries.forEachIndexed { index, query ->
            var count = 0
            points.forEach {
                val des = sqrt(pow((it[0] - query[0]).toDouble(), 2.0) + pow((it[1] - query[1]).toDouble(), 2.0))
                if(des <= query[2]){
                    count++
                }
            }
            res[index] = count
        }
        return res
    }
}