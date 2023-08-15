package com.derry.kt_coroutines.t93

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.jvm.internal.Intrinsics

suspend fun request1() = suspendCoroutine<Int> {

}

suspend fun request2() = suspendCancellableCoroutine<Int> {
    it.invokeOnCancellation {

    }
}

// TODO 验证这个很长的方法 suspendCoroutineUninterceptedOrReturn

// 伪挂起
private suspend fun testNoSuspendAction(): Int
 = kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn<Int> {
    "Derry"
    9999999 // Lambda最后一行最为放回值
    // return@suspendCoroutineUninterceptedOrReturn 8888888
}

// 真正的挂起
private suspend fun testSuspendAction()
 = kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn<Double> {
     object: Thread() {
         override fun run() {
             super.run()
             sleep(3000L)
             it.resume(87687686.6)
         }
     }.start()
     kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED // 代表挂起的标记   // Lambda最后一行最为放回值
     // return@suspendCoroutineUninterceptedOrReturn kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED // 代表挂起的标记
}

// 用户
fun main() = runBlocking<Unit> {
    // 伪挂起
    val r = testNoSuspendAction()
    println("r:$r")


    // 真正的挂起
    val r2 = testSuspendAction()
    println("r2:$r2")
}