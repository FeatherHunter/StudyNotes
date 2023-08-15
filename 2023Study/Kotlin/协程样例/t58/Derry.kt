package com.derry.kt_coroutines.t58

import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.runBlocking

// TODO 58-Android协程Flow的reduce
fun main() = runBlocking<Unit> {

    // 末端操作符 reduce  collect

    val r = /*arrayOf(1, 2, 4, 5, 6, 7, 8, 9, 10)*/ (1..100)
        .asFlow()
        // .map { it * it }
        .reduce { p1, p2 ->
            val result = p1 + p2
            result
        }
    println(r)

    var result  = 0
    for (i in 1..100) {
        result += i
    }
    println(result)
}