package com.derry.kt_coroutines.t82

import kotlinx.coroutines.*
import kotlin.concurrent.thread
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

// 此函数的类型是：(Double) -> String
fun action1(number: Double): String {
    return "Derry1"
}

// 此函数的类型是：suspend (Double) -> String
suspend fun action1Suspend(number: Double): String {
    withContext(Dispatchers.IO) {
        delay(2000L)
    }
    return "Derry1"
}

// 模拟编译期转换后的大概样子
interface ICallback {
    fun successful(result: String)
}
fun action1Suspend2(number: Double, iCallback: ICallback): Any ? {
    thread {
        Thread.sleep(2000L)
    }
    iCallback.successful("Derry1") // 接口回调
    return Unit
}

// 模拟编译期转换后的大概样子
fun action1Suspend3(number: Double, continuation: Continuation<String>): Any ? {
    thread {
        Thread.sleep(2000L)
    }
    continuation.resume("Derry1")  // 接口回调
    return Unit
}

// 分析了Continuation源码 对比 ICallback 几乎一模一样

// 不信的话，用Java代码调用 action1Suspend，就明白了 （所有的 suspend 挂起函数，都有一个隐式的Continuation）

fun customCoroutine(lambda: suspend () -> Unit) {}

fun main() /*= runBlocking<Unit>*/ {
    val r1 : (Double) -> String = ::action1
    val r2 : suspend (Double) -> String = ::action1Suspend

    // 挂起函数 与 普通函数 是完全不同的类型，所以两者之间不能相互赋值
    /*val r3 : suspend (Double) -> String = ::action1
    var r4 : (Double) -> String = ::action1Suspend*/

    // 报错的原因：为什么 非挂起函数 或 非协程 不能调用挂起函数 ？
    // 答：因为 非挂起函数 或 非协程 没有隐式的Continuation，所以报错

    // 凭什么挂起函数，只能被 协程 或 挂起函数 调用？
    // 答：因为 协程 或 挂起函数 有一个 suspend，会通过Kotlin编译期 隐式的转换成 Continuation 传给挂起函数
    // action1Suspend(86858676.6)
    customCoroutine {
        action1Suspend(86858676.6)
    }

    // 挂起的概念：
    // 1.挂起与恢复 是协程的底层实现
    // 2.挂起与恢复的上层实现是挂起 suspend fun 来完成了

    // Continuation是剩余没有执行 需要恢复执行的代码的负责
    GlobalScope.launch {
        val response1 = action1Suspend(8688788.7) // 异步线程请求发起中...
        // Main线程更新UI
        // ...
        val response2 = action1Suspend(8789789.7) // 异步线程请求发起中...
        // Main线程更新UI
        // ...
        val response3 = action1Suspend(5666465.7) // 异步线程请求发起中...
        // Main线程更新UI
        // ...
    }

    Thread.sleep(1000L)
}