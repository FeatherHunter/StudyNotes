package com.derry.kt_coroutines.t90

import kotlinx.coroutines.delay
import kotlin.coroutines.*

// 案例一 底层写法
/*suspend fun getLength(str: String) : Int = suspendCoroutine<Int> {
    object: Thread() {
        override fun run() {
            super.run()
            sleep((5000L..20000L).random())
            it.resume(str.length) // 调用Continuation回调 最终的成果长度 给 用户
        }
    }.start()
    println("真正读取中...")
}*/

// 案例一  标准写法
suspend fun getLength(str: String) : Int {
    println("真正读取中...")
    delay((5000L..20000L).random())
    return str.length // 调用Continuation回调 最终的成果长度 给 用户
}

fun main() {

    // 情况一
    val typeFun1 : suspend (String) -> Int = ::getLength

    // 情况二 思路没有问题，只是语法是编译后的样子，所以不支持而已
    // val typeFun2 : (String, Continuation<Int>) -> Any? = ::getLength

    val typeFun3 : (String, Continuation<Int>) -> Any? /*: suspend (String) -> Int*/ = ::getLength as (String, Continuation<Int>) -> Any?

    typeFun3("Derry", object: Continuation<Int> {
        override val context: CoroutineContext
            get() = EmptyCoroutineContext

        override fun resumeWith(result: Result<Int>) {
            println(" 案例一 读完完成 长度是:${result.getOrThrow()}")
        }
    })

    Thread.sleep(21000L)
}