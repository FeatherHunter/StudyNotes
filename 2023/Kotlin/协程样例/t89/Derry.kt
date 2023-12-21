package com.derry.kt_coroutines.t89

import kotlinx.coroutines.*
import kotlin.concurrent.thread
import kotlin.coroutines.*

// 案例一
suspend fun getLength(str: String) : Int = suspendCoroutine<Int> {
    object: Thread() {
        override fun run() {
            super.run()
            sleep((5000L..20000L).random())
            it.resume(str.length) // 调用Continuation回调 最终的成果长度 给 用户
        }
    }.start()
    println("真正读取中...")
}

// TODO  ========================================

// 案例二
suspend fun getLength2(str: String) : Int  {
    withContext(Dispatchers.IO) {
        delay((5000L..20000L).random())
    }
    return str.length  // 调用Continuation回调 最终的成果长度 给 用户
}

// 用户
fun main() {
    // 案例一 的 表现
    suspend {
        getLength("Derry")
    }.startCoroutine(object : Continuation<Int> {
        override val context: CoroutineContext
            get() = EmptyCoroutineContext

        override fun resumeWith(result: Result<Int>) {
            println(" 案例一 读完完成 长度是:${result.getOrThrow()}")
        }
    })

    GlobalScope.launch {
        val result = getLength("Derry2") // Kotlin编译器 做了大量大量海量的处理，你看不到
        println(" 案例一 读完完成 长度是:${result}")
    }

    // TODO  ========================================
    // 案例二  的 表现
    suspend {
        getLength2("DDD")
    }.startCoroutine(object : Continuation<Int> {
        override val context: CoroutineContext
            get() = EmptyCoroutineContext

        override fun resumeWith(result: Result<Int>) {
            println("案例二 读完完成 长度是:${result.getOrThrow()}")
        }
    })

    GlobalScope.launch {
        val result = getLength2("DDD")
        println("案例二 读完完成 长度是:${result}")
    }

    Thread.sleep(21000L)
}