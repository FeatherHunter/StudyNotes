package LeetCode

import kotlin.math.max
import kotlin.math.min


/**
 *
 * [3,0,8,4]
 * [2,4,5,7]
 * [9,2,6,3]
 * [0,3,1,0]
 * gridNew = [ [8, 4, 8, 7],
 *             [7, 4, 7, 7],
 *             [9, 4, 8, 7],
 *             [3, 3, 3, 3] ]
 *
 */
class P807_保持城市天际线KT {
    fun maxIncreaseKeepingSkyline(grid: Array<IntArray>): Int {
        val arr = grid.map { it.clone() }.toTypedArray()
        var sum = 0
        for (i in 0 until arr.size){
            for (j in 0 until arr[i].size){
                var newval = Int.MAX_VALUE
                var col = 0
                for (t in 0 .. arr.size - 1){
                    col = max(col, grid[t][j])
                }
                newval = min(newval, col)

                var row = 0
                for (t in 0 .. arr[i].size - 1){
                    row = max(row, grid[i][t])
                }
                newval = min(newval, row)

                arr[i][j] = newval
                println("newval=$newval grid[i][j]=${grid[i][j]}")
                sum += newval - grid[i][j]
            }
        }
        return sum
    }
}