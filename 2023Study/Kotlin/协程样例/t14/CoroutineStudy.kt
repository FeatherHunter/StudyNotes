package com.derry.kt_coroutines.t14

import kotlinx.coroutines.*

// 先 [请求 用户数据] 网络请求耗时10秒钟
private suspend fun requestLoadUser() = withContext(Dispatchers.IO) { delay(1000 * 10) }

// 再 [请求 用户资产数据] 网络请求耗时6秒钟
private suspend fun requestLoadUserAssets() = withContext(Dispatchers.IO) { delay(1000 * 6) }

// 再 [请求 用户资产详情数据] 网络请求耗时3秒钟
private suspend fun requestLoadUserAssetsDetails() = withContext(Dispatchers.IO) { delay(1000 * 3) }

fun main() = runBlocking<Unit> {
    // Derry以后的作风：保姆式教学
    /*val job1 = launch {
        println(111) // ②
        requestLoadUser()
        println("[请求 用户数据] 第一次请求 Successful") // ③
    }

    // 协程join 挂起
    println("第一次请求准备开始中>>>")  // ①
    job1.join()
    println("第一次请求全部完成<<<") // 4

    val job2 = launch {
        println(222) // 6
        requestLoadUserAssets()
        println("[请求 用户资产数据] 第二次请求 Successful") // 7
    }

    // 协程join 挂起
    println("第二次请求准备开始中>>>") // 5
    job2.join()
    println("第二次请求全部完成<<<") // 8

    val job3 = launch {
        println(333) // 10
        requestLoadUserAssetsDetails()
        println("[请求 用户资产详情数据] 第三次请求 Successful") // 11
    }

    // 协程join 挂起
    println("第三次请求准备开始中>>>") //9
    job3.join()
    println("第三次请求全部完成<<<") // 12*/


    /*val deferred1 = async {
        println(111) // 2
        requestLoadUser()
        println("[请求 用户数据] 第一次请求 Successful") // 3
        "一号大哥"
    }

    // 协程join 挂起
    println("第一次请求准备开始中>>>")  // 1
    println(deferred1.await()) // deferred1.join()
    println("第一次请求全部完成<<<")  // 4

    val deferred2 = async {
        println(222) // 6
        requestLoadUserAssets()
        println("[请求 用户资产数据] 第二次请求 Successful") // 7
        "二号大哥"
    }

    // 协程join 挂起
    println("第二次请求准备开始中>>>") // 5
    println(deferred2.await()) // deferred2.join()
    println("第二次请求全部完成<<<") // 8

    val deferred3 = async {
        println(333) // 10
        requestLoadUserAssetsDetails()
        println("[请求 用户资产详情数据] 第三次请求 Successful") // 11
        "三号大哥"
    }

    // 协程join 挂起
    println("第三次请求准备开始中>>>") //9
    println(deferred3.await()) // deferred3.join()
    println("第三次请求全部完成<<<") // 12*/

    // 前面说过，runBlocking<Unit> ，是非阻塞式， 会阻塞 main thread 而不是 协程体，所以我们的协程体 还是 非阻塞

    // 又有一个纠结点，等下讲

    val d = async {
        println("async start") // 2
        delay(10000)
        println("async end") // 3
        "老大" // 4
    }

    println("main 111") // 1
    println(d.await()) // 5
    println("main 222") // 6
}