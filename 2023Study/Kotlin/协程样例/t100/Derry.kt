package com.derry.kt_coroutines.t100

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlin.coroutines.*

suspend fun requestAction() = suspendCoroutine<Int> {
    println("requestAction ${it} thread:${Thread.currentThread().name}")
    Thread.sleep(2000L)
    // 假设这里做了很多工作
    // ...
    it.resume(2000000)
    it.resume(3000000) // result:Failure(java.lang.IllegalStateException: Already resumed)
}

fun main() {

    val continuationBenti : Continuation<Unit> = suspend {
        println("suspend{} thread:${Thread.currentThread().name}")
        delay(2000L)
        // 假设这里做了很多工作
        // ...

        // 1000000

        requestAction()
    }.createCoroutine(object: Continuation<Int> {
        override val context: CoroutineContext
            get() = Dispatchers.Default

        override fun resumeWith(result: Result<Int>) {
            println("resumeWith result:${result} thread:${Thread.currentThread().name}")
        }
    })


    // SafeContinuation.resume(Unit)
    continuationBenti.resume(Unit)
    // continuationBenti.resume(Unit) // 多次调用会抛出这个异常  Already resumed

    println("开始准备执行中..")

    println("continuationBenti:$continuationBenti")

    Thread.sleep(5000L)
}