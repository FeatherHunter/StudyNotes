package com.derry.kt_coroutines.t28

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull

// TODO 28-Android协程超时的任务
fun main() = runBlocking <Unit> {
    /*withTimeout(6000) {
        for (i in 1 .. Int.MAX_VALUE) {
            println("for i:$i")
            delay(500)
        }
    }*/

    val r = withTimeoutOrNull(6000) {
        for (i in 1 .. 6) {
            println("for i:$i")
            delay(500)
        }
        "执行完成"
    }
    println("成果是: ${r ?: "执行出现问题"}")
}