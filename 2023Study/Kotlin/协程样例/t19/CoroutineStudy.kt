package com.derry.kt_coroutines.t19

import kotlinx.coroutines.*

// TODO t19 -- 19-Android取消协程当前的作用域

// runBlocking会 阻塞主线程 来 等待所有的子协程任务全部结束
fun main() = runBlocking<Unit> // 父协程
{
    // TODO 在当前 runBlocking父协程 作用域 中 取消，会直接到 所有子协程取消
    /*// 子线程 用 父协程 协程作用域
    launch {
        println("launch start 1")
        delay(1000)
        println("launch end 1")
    }

    // 子线程 用 父协程 协程作用域
    launch {
        println("launch start 2")
        delay(2000)
        println("launch end 2")
    }

    // delay(50)
    cancel() // 在父协程中 取消 抛出 JobCancellationException，所有子协程会跟着取消*/

    // TODO 不用 runBlocking 的 协程协程作用域，自建协程作用域
    val scope : CoroutineScope = CoroutineScope(context = Dispatchers.Default)
    // 调度前
    println("调度前")

    scope.launch {
        // println("launch start 1") // 调度后
        delay(1000)
        println("launch end 1")
    }

    // 子线程 用 父协程 协程作用域
    scope.launch {
        // println("launch start 2") // 调度后
        delay(2000)
        println("launch end 2")
    }

    // scope.cancel()

    // main thread 结束了   runBlocking压根就不会等  我们自己的scope，为什么？ 因为 runBlocking只会等自己的子协程

    delay(5000)
}