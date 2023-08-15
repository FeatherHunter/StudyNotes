package com.derry.kt_coroutines.t20

import kotlinx.coroutines.*

// TODO 20-Android Job取消兄弟的协程
fun main() = runBlocking<Unit> {

    // TODO 不用 runBlocking 的 协程协程作用域，自建协程作用域
    val scope : CoroutineScope = CoroutineScope(context = Dispatchers.Default)
    // 调度前
    println("调度前")

    val loginRequestJob = scope.launch {
        // println("launch start 1") // 调度后
        delay(1000)
        println("launch end 1")
    }

    // 子线程 用 父协程 协程作用域
    val registerRequestJob = scope.launch {
        // println("launch start 2") // 调度后
        delay(2000)
        println("launch end 2")
    }

    // scope.cancel() 会统一取消

    loginRequestJob.cancel() // 不会影响兄弟协程 registerRequestJob

    // main thread 结束了   runBlocking压根就不会等  我们自己的scope，为什么？ 因为 runBlocking只会等自己的子协程

    delay(5000)
}