package com.derry.kt_coroutines.t101

import kotlinx.coroutines.*
import kotlin.coroutines.*

// TODO test createCoroutine   receiver: R    (suspend R.() -> T).createCoroutine

/*@RestrictsSuspension
@SinceKotlin("1.3")*/
class DerryScope

suspend fun DerryScope.mDelay() {
    Thread.sleep(2000L)
}

fun suspendAction0() : Int = 200 // 普通函数

suspend fun suspendAction1() : Int = 600 // 挂起函数

suspend fun DerryScope.suspendAction2() : Int = 1000 // 绑定了作用域DerryScope的 挂起函数

fun test(lambda: suspend DerryScope.() -> Int) {
    /*lambda.createCoroutine(DerryScope(), object: Continuation<Int> {
        override val context: CoroutineContext
            get() = Dispatchers.IO

        override fun resumeWith(result: Result<Int>) {
            println("resumeWith2 result:$result")
        }
    }).resume(Unit)*/

    lambda.startCoroutine(DerryScope(), object: Continuation<Int> {
        override val context: CoroutineContext
            get() = Dispatchers.IO

        override fun resumeWith(result: Result<Int>) {
            println("resumeWith2 result:$result")
        }
    })
}


suspend fun main() {

    // suspend () -> Int ===== (suspend () -> T).createCoroutine
    suspend {
        println("suspend{}1 thread:${Thread.currentThread().name}")
        delay(2000L)
        // 假设这里做了很多事情
        //...

        99
    }.createCoroutine(object: Continuation<Int> {
        override val context: CoroutineContext
            get() = Dispatchers.Default

        override fun resumeWith(result: Result<Int>) {
            println("resumeWith1 result:$result")
        }
    }).resume(Unit)


    // TODO test createCoroutine   receiver: R    (suspend R.() -> T).createCoroutine

    test {
        println("suspend{}2 thread:${Thread.currentThread().name}")
        mDelay()
        // 假设这里做了很多事情
        //...

        // suspendAction0() // 协程调用 非挂起函数  都是不再DerryScope作用域的 【他虽然不是 DerryScope作用域的，但是他不属于挂起函数】
        // suspendAction1() // 协程调用 挂起函数  都是不再DerryScope作用域的  【因为此挂起函数，没有在DerryScope作用域中，所以报错】
        suspendAction2() // 协程调用 挂起函数  在DerryScope作用域的
    }

    /*
    GlobalScope.launch {  }

    sequence<Int> {

    }*/

    delay(5000L) // 等待上面的代码
}