package com.derry.kt_coroutines.t13

import kotlinx.coroutines.*

// 非安卓平台程序

// = runBlocking<Unit> { 把 main thread 编程 在 main thread 依附执行的 协程
fun main() = runBlocking<Unit> {

    /*GlobalScope.launch {
        println("coroutine launch start")
        delay(10000)
        println("coroutine launch end")
    }

    // main thread 是不可能等，协程体的，所以下面的代码，是等一等 协程体

    Thread.sleep(11000) // 11秒 main thread 不要执行完*/

    // 子协程
    val job = launch {
        println("coroutine launch start thread:${Thread.currentThread().name}") // ②  // 他到底是不是第一次打印，就会非常纠结，因为以前说的是 协程非阻塞 而现在 runBlocking是阻塞的
        delay(10000)
        println("coroutine launch end thread:${Thread.currentThread().name}") // ④
    }

    // 子协程
    val deferred = async {
        println("coroutine async start thread:${Thread.currentThread().name}") // ③
        delay(11000)
        println("coroutine async end thread:${Thread.currentThread().name}") // ⑤
        "李元霸"
    }

    println("deferred 零 test derry thread:${Thread.currentThread().name}") // ①

    println("deferred 一 :${deferred.await()}") // ⑥ // TODO 他是不是 第二个打印，我们说不是，为什么，因为他是挂起函数 挂起多久 什么时候恢复 等下讲

    delay(300) // 睡眠300毫秒

    println("deferred 二 :${deferred.await()}") // ⑦

    // 父协程 等 子协程
    // 一般学习和测试会用 runBlocking，因为测试和学习非常方便，不需要额外去 睡眠等待 协程体
}