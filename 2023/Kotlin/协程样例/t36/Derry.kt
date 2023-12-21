package com.derry.kt_coroutines.t36

import kotlinx.coroutines.*
import java.lang.Exception

// TODO 36-Android协程supervisorJob
fun main() = runBlocking <Unit> {

    val scope = CoroutineScope(SupervisorJob())

    val j1 = scope.launch {
        println("launch1 start ...")
        delay(1000)
        throw KotlinNullPointerException("is null")
    }

    val j2 = scope.launch {
        println("launch2 start ...")
        delay(1300)
        println("launch2 end ...")
    }

    val j3 = scope.launch {
        println("launch3 start ...")
        delay(2300)
        println("launch3 end ...")
    }

    val j4 = scope.launch {
        println("launch4 start ...")
        delay(2300)
        println("launch4 end ...")
    }

    val j5 = scope.launch {
        println("launch5 start ...")
        delay(2300)
        println("launch5 end ...")
    }

    // 2秒后的协程必须全部取消
    delay(2000)
    scope.cancel()

    // runBlocking 协程作用域，会等待， j1，j2 的协程
    // joinAll(j1, j2, j3, j4, j5)
}